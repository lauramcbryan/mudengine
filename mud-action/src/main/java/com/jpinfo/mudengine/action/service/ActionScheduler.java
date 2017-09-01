package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.exception.ActionRefusedException;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCost;
import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionInfo;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;

@Service
public class ActionScheduler {
	
	private static Long currentTurn = 0L;
	
	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private MudActionClassRepository classRepository;
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Scheduled(fixedRate=10000)
	public void updateActions() {
		
		System.out.println("ActionScheduler.  Turn=" + ActionScheduler.currentTurn);
		
		// List of processed actors in this iteraction
		List<Long> processedActors = new ArrayList<Long>();

		// List of pending and active actions
		List<MudAction> pendingActions = repository.findPendingActions();
		
		for(MudAction curPendingAction: pendingActions) {
			
			// Check if there´s another action running for the same actor
			// That need to be done in the case we already started an action for the same actor in this iteration
			if (processedActors.indexOf(curPendingAction.getActorCode())==-1) {
				
				curPendingAction = updateAction(curPendingAction);
				
				repository.save(curPendingAction);

				processedActors.add(curPendingAction.getActorCode());
				
			} // endif
		} // next pendingAction
		
		ActionScheduler.currentTurn++;
	}
	
	/**
	 * This method does:
	 * - Retrieve a list with all actions that are in PENDING state
	 * - Update them to NotStarted
	 */
	private MudAction updateAction(MudAction curAction) {
		
		switch(curAction.getCurrStateEnum()) {
		
		case NOT_STARTED: {
			
			try {
				
				ActionInfo fullActionState = buildAction(curAction);
			
				// Check the prerequisites
				checkPrerequisites(fullActionState);
				
				// Update the action to Started
				curAction.setCurrState(Action.EnumActionState.STARTED);
				curAction.setStartTurn(ActionScheduler.currentTurn);
	
				// Calculates the endTurn (it´s included in costs)
				fullActionState = calculateCost(fullActionState);
				
				// After this, the endTurn is set in action object
				curAction.setEndTurn(fullActionState.getEndTurn());
				
			} catch (ActionRefusedException e) {
				
				// Update the action to Refused
				curAction.setCurrState(Action.EnumActionState.REFUSED);
				curAction.setStartTurn(ActionScheduler.currentTurn);
				curAction.setEndTurn(ActionScheduler.currentTurn);
			}
			
			break;
		}
		case STARTED: {
			
			try {
				ActionInfo fullActionState = buildAction(curAction);
	
				// Calculate effects
				fullActionState = calculateEffect(fullActionState);
				
				// TODO: Update changed entities
				
				// Update the action to COMPLETED
				curAction.setCurrState(Action.EnumActionState.COMPLETED);
				
			} catch (ActionRefusedException e) {
				
				// Update the action to Refused
				curAction.setCurrState(Action.EnumActionState.CANCELLED);
				curAction.setStartTurn(ActionScheduler.currentTurn);
				curAction.setEndTurn(ActionScheduler.currentTurn);
			}
			
			break;
		}
		default:
		}

		return curAction;
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

				// TODO: Update the message queue
				
				throw new ActionRefusedException(curPrereq.getMessageCode());
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
			
			// TODO: Update the message queue
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
		
		/**
		 * Items of being:
		 * It´s disabled by now because I should have all being items retrieved at this time to retrieve their reactions
		 * and that could be expensive.
		 */
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
	
	private ActionInfo buildAction(MudAction a) throws ActionRefusedException {
		
		ActionInfo result = new ActionInfo();
		
		String token = TokenService.buildInternalToken();

		result.setActionId(a.getActionId());
		result.setActionCode(a.getActionCode());
		
		//Actor
		if (a.getActorCode()!=null) {
			
			Being actor = beingService.getBeing(token, a.getActorCode());
			
			if (actor!=null) {
				result.setActor(actor);
			} else {
				//throw new ActionRefusedException(actionTargetClass.getName() +"  " + Id + " not found");
				throw new ActionRefusedException(ActionRefusedException.GENERIC_ERROR);
			}
		}
		
		// Mediator
		if (a.getMediatorCode()!=null) {
			
			Item mediator = itemService.getItem(a.getMediatorCode());
			
			if (mediator!=null) {
				result.setMediator(mediator);
			} else {
				//throw new ActionRefusedException(actionTargetClass.getName() +"  " + Id + " not found");
				throw new ActionRefusedException(ActionRefusedException.GENERIC_ERROR);
			}
		}
		
		// Place
		if (a.getPlaceCode()!=null) {
			
			Place place = placeService.getPlace(a.getPlaceCode());
			
			if (place!=null) {
				result.setPlace(place);
			} else {
				//throw new ActionRefusedException(actionTargetClass.getName() +"  " + Id + " not found");
				throw new ActionRefusedException(ActionRefusedException.GENERIC_ERROR);
			}
		}
		
		if (a.getTargetCode()!=null) {
			
			ActionTarget target = null;
			
			switch(a.getTargetTypeEnum()) {
			case ITEM:
				target = itemService.getItem(Long.valueOf(a.getTargetCode()));
				break;
			case PLACE:
				target = placeService.getPlace(Integer.valueOf(a.getTargetCode()));
				break;
			case BEING:
				target = beingService.getBeing(token, Long.valueOf(a.getTargetCode()));
				break;
			}
			
			if (target!=null) {
				result.setTarget(target);
			}
		}
		
		return result;
	}

}
