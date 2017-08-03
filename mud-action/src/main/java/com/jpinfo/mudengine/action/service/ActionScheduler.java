package com.jpinfo.mudengine.action.service;

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
import com.jpinfo.mudengine.common.action.ActionSimpleState;
import com.jpinfo.mudengine.common.action.ActionState;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

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
		
		
		
		updatePendingActions();
		
		updateActiveActions();
		updatePendingMessages();
	
		
		ActionScheduler.currentTurn++;
	}
	
	/**
	 * This method does:
	 * - Retrieve a list with all actions that are in PENDING state
	 * - Update them to NotStarted
	 */
	private void updatePendingActions() {
		
		// Obter uma lista dos actions que não foram iniciadas ainda
		List<MudAction> pendingActions = repository.findStartableActions();
		
		for(MudAction curPendingAction: pendingActions) {
			
			// Check if there´s another action running for the same actor
			// That need to be done in the case we already started an action for the same actor in this iteration
			MudAction dummy = repository.findFirstOneByCurrStateAndActorCode(ActionSimpleState.STARTED, curPendingAction.getActorCode());
			
			if (dummy==null) {
				
				try {
					// Check the prerequisites
					ActionState fullActionState = buildAction(curPendingAction);
					
					checkPrerequisites(fullActionState);
					
					// Update the action to Started
					curPendingAction.setCurrState(ActionSimpleState.STARTED);
					curPendingAction.setStartTurn(ActionScheduler.currentTurn);
					
					// Calculates the endTurn (it´s included in costs)
					fullActionState = calculateCost(fullActionState);
					
					// After this, the endTurn is set in action object
					curPendingAction.setEndTurn(fullActionState.getEndTurn());
					
					// Calculate previous reactions to the action
					calculateReactions(fullActionState, true);
					
				} catch (ActionRefusedException e) {
					
					// Update the action to Refused
					curPendingAction.setCurrState(ActionSimpleState.REFUSED);
					curPendingAction.setStartTurn(ActionScheduler.currentTurn);
					curPendingAction.setEndTurn(ActionScheduler.currentTurn);
				}
				
				//repository.save(curPendingAction);
			} // endif
		} // next pendingAction
		
	}
	
	
	private void updateActiveActions() {
		
		// Obter uma lista das actions Ativas e que estão terminando
		List<MudAction> actionList = repository.findFinishedActions(ActionScheduler.currentTurn);
		
		for(MudAction curAction: actionList) {
			
			try {
			
				// Apply the effects
				ActionState fullActionState = buildAction(curAction);
				
				fullActionState = calculateEffect(fullActionState);
				
				// TODO: Update changed entities
				
				// Update the action to COMPLETED
				curAction.setCurrState(ActionSimpleState.COMPLETED);
				
				// Calculate reactions to the action
				calculateReactions(fullActionState, false);
				
				
			} catch(ActionRefusedException e) {
				
				// Update the action to Cancelled
				curAction.setCurrState(ActionSimpleState.CANCELLED);
				curAction.setEndTurn(ActionScheduler.currentTurn);
			}
			
			repository.save(curAction);
		}
	}
	
	private void updatePendingMessages() {
		
		// Obter uma lista das mensagens pendentes para envio cujos clientes suportam SSE
		
		// Enviar a mensagem
	}
	
	private void checkPrerequisites(ActionState e) throws ActionRefusedException {
		
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
	
	private ActionState calculateCost(ActionState e) {
		
		MudActionClass action = classRepository.findOne(e.getActionCode());
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);
		
		for(MudActionClassCost curCost: action.getCostList()) {
			
			// Running cost expressions
			Expression curExpression = parser.parseExpression(curCost.getExpression());
			
			e = curExpression.getValue(context, ActionState.class);
			
			// TODO: Update the message queue
		}
		
		return e;
	}
	
	private ActionState calculateEffect(ActionState e) {
		
		MudActionClass action = classRepository.findOne(e.getActionCode());
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);
		
		for(MudActionClassEffect curEffect: action.getEffectList()) {
			
			// Running effect expressions
			Expression curExpression = parser.parseExpression(curEffect.getExpression());
			
			e = curExpression.getValue(context, ActionState.class);
			
			// TODO: Update the message queue
			
			// TODO: Mark the changed entities for update
			
		}
		
		return e;
	}
	
	private ActionState calculateReactions(ActionState e, boolean isBefore) {
		
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
			
			effectExpression.getValue(context, ActionState.class);
		}
		
		return context;
		
	}
	
	private ActionState buildAction(MudAction a) throws ActionRefusedException {
		
		ActionState result = new ActionState();

		result.setActionId(a.getActionId());
		result.setActionCode(a.getActionCode());
		
		//Actor
		if (a.getActorCode()!=null) {
			
			Being actor = beingService.getBeing(a.getActorCode());
			
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
			
			switch(a.getTargetType()) {
			case "ITEM":
				target = itemService.getItem(Long.valueOf(a.getTargetCode()));
				break;
			case "PLACE":
				target = placeService.getPlace(Integer.valueOf(a.getTargetCode()));
				break;
			case "BEING":
				target = beingService.getBeing(Long.valueOf(a.getTargetCode()));
				break;
			}
			
			if (target!=null) {
				result.setTarget(target);
			}
		}
		
		return result;
	}

}
