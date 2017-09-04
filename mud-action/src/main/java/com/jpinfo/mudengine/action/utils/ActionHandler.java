package com.jpinfo.mudengine.action.utils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.exception.ActionRefusedException;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCost;
import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionType;
import com.jpinfo.mudengine.common.interfaces.Reaction;

@Component
public class ActionHandler {
	
	@Autowired
	private MudActionClassRepository classRepository;

	public void updateAction(Long currentTurn, Action curAction, ActionInfo fullActionState) {

		switch(curAction.getCurState()) {
		
		case NOT_STARTED: {
			
			try {
				
				// Set the start turn to check
				curAction.setStartTurn(currentTurn);
				
				// Check the prerequisites
				checkPrerequisites(fullActionState);

				// Calculates the endTurn (it´s included in costs)
				fullActionState = calculateCost(fullActionState);
				
				// Update the action to Started
				curAction.setCurState(Action.EnumActionState.STARTED);
				
				// After this, the endTurn is set in action object
				curAction.setEndTurn(fullActionState.getEndTurn());
				
			} catch (ActionRefusedException e) {
				
				// Update the action to Refused
				curAction.setCurState(Action.EnumActionState.REFUSED);
				curAction.setEndTurn(currentTurn);
			}
			
			break;
		}
		case STARTED: {
			
			if (curAction.getActionType().equals(EnumActionType.CONTINUOUS)) {
				
				try {
				
					// Recheck prerequisites
					checkPrerequisites(fullActionState);
				
					// Reapply effects
					fullActionState = calculateEffect(fullActionState);
					
				} catch (ActionRefusedException e) {
					
					// Update the action to Refused
					curAction.setCurState(Action.EnumActionState.COMPLETED);
					curAction.setEndTurn(currentTurn);
				}
				
			} else {
				
				// If the action is completed
				if (curAction.getEndTurn()<=currentTurn) {
					
					// Apply effects
					fullActionState = calculateEffect(fullActionState);
					
					// Update the effect to completed
					curAction.setCurState(Action.EnumActionState.COMPLETED);
				}
			}
			
			break;
		}
		default:
		}
	}
	
	public void checkPrerequisites(ActionInfo e) throws ActionRefusedException {
		
		MudActionClass action = classRepository.findOne(e.getActionCode());
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);
		
		for(MudActionClassPrereq curPrereq: action.getPrereqList()) {

			// Running prereq expressions
			Expression curExpression = parser.parseExpression(curPrereq.getExpression());
			
			boolean accepted = curExpression.getValue(context, Boolean.class);
			
			if (!accepted) {

				// Update the message queue
				e.sendMessageTo(e.getActorCode(), Action.EnumTargetType.BEING, curPrereq.getMessageCode(), new Object[] {});
				
				throw new ActionRefusedException();
			}
		}
		
	}
	
	private ActionInfo calculateCost(ActionInfo e) {
		
		MudActionClass action = classRepository.findOne(e.getActionCode());
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);
		
		for(MudActionClassCost curCost: action.getCostList()) {
			
			// Running cost expressions
			Expression curExpression = parser.parseExpression(curCost.getExpression());
			
			e = curExpression.getValue(context, ActionInfo.class);
		}
		
		return e;
	}
	
	private ActionInfo calculateEffect(ActionInfo e) {
		
		MudActionClass action = classRepository.findOne(e.getActionCode());
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);
		
		for(MudActionClassEffect curEffect: action.getEffectList()) {
			
			// Running effect expressions
			Expression curExpression = parser.parseExpression(curEffect.getExpression());
			
			e = curExpression.getValue(context, ActionInfo.class);
			
			// TODO: Update the message queue
			
			// TODO: Mark the changed entities for update
			
		}
		
		return e;
	}
	
	private ActionInfo calculateReactions(ActionInfo e, boolean isBefore) {
		
		EvaluationContext context = new StandardEvaluationContext(e);
		
		// TODO: Apply effects caused by actor
		for (Reaction curReaction: e.getActor().getReactions(e.getActionCode(), isBefore)) {
			applyReaction(context, curReaction);
		}
		
		 // Items of being:
		 // It´s disabled by now because I should have all being items retrieved at this time to retrieve their reactions
		 // and that could be expensive.
		/*
		for(BeingItem curItem: e.getActor().getItems().values()) {
			
			for(Reaction curReaction: curItem.getReactions(e.getActionCode(), isBefore)) {
				
				applyReaction(context, curReaction);
			}
		}
		*/

		
		// TODO: Apply effects caused by target
		for (Reaction curReaction: e.getTarget().getReactions(e.getActionCode(), isBefore)) {
			applyReaction(context, curReaction);
		}
		
		
		// TODO: Apply effects caused by mediator (if present)
		if (e.getMediator()!=null) {
			
			for (Reaction curReaction: e.getMediator().getReactions(e.getActionCode(), isBefore)) {
				applyReaction(context, curReaction);
			}
		}
		
		
		return e;
	}
	
	private EvaluationContext applyReaction(EvaluationContext context, Reaction reaction) {

		ExpressionParser parser = new SpelExpressionParser();
		
		// Running prereq expressions
		Expression prereqExpression = parser.parseExpression(reaction.getPrereq());
		
		boolean accepted = prereqExpression.getValue(context, Boolean.class);
			
		if (accepted) {
			// apply the effect
			
			Expression effectExpression = parser.parseExpression(reaction.getExpression());
			
			effectExpression.getValue(context, ActionInfo.class);
		}
		
		return context;
		
	}
	
}
