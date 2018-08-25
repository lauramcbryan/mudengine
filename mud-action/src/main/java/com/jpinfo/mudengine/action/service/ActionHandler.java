package com.jpinfo.mudengine.action.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.MessageServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.dto.BeingComposite;
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.converter.ActionInfoConverter;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.action.ActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;
import com.jpinfo.mudengine.common.exception.ActionRefusedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Component
public class ActionHandler {

	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private MessageServiceClient messageService;
	
	@Autowired
	private ActionInfoConverter actionInfoConverter;
	
	public void runActions(Long currentTurn, List<MudAction> actionList) {
		
		actionList.stream().forEach(curPendingAction -> {
			
			try {
				ActionInfo fullState = actionInfoConverter.build(curPendingAction);
				
				runOneAction(currentTurn, fullState);
				
				curPendingAction.setStartTurn(fullState.getStartTurn());
				curPendingAction.setEndTurn(fullState.getEndTurn());
				curPendingAction.setCurrState(fullState.getCurState().ordinal());
				
			} catch(EntityNotFoundException e) {
				curPendingAction.setCurrStateEnum(EnumActionState.CANCELLED);
				curPendingAction.setEndTurn(currentTurn);
			}
			
			repository.save(curPendingAction);
		});
		
	}
	
	public void runOneAction(Long currentTurn, ActionInfo fullActionState) {
		
		try {
			
			// If we are initiating an action right now, update the fields
			if (!fullActionState.hasInitiated()) {
				initiateAction(currentTurn, fullActionState);
			}
			
			// Check prerequisites
			checkPrerequisites(fullActionState);

			// Update the action state to RUNNING
			fullActionState.setCurState(Action.EnumActionState.STARTED);

			// Apply the effects if needed
			if (fullActionState.needToApplyEffects(currentTurn))
			{
				applyEffects(fullActionState);
				
				// Update changed entities
				updateEntities(fullActionState);
			}

			// if reach end_turn, complete it
			if (fullActionState.isFinished(currentTurn)) {

				// Update the action to completed
				fullActionState.setCurState(Action.EnumActionState.COMPLETED);
			}
			
			// Send the message in the message queue
			sendMessages(fullActionState);
			
			
		} catch (ActionRefusedException e) {

			// If the action was not started, update it to REFUSED
			if (!fullActionState.hasInitiated())
				fullActionState.setCurState(Action.EnumActionState.REFUSED);
			else
				// If it was started, update it to COMPLETE
				fullActionState.setCurState(Action.EnumActionState.COMPLETED);
			
			fullActionState.setEndTurn(currentTurn);
			
		} catch(EntityNotFoundException e) {
			
			fullActionState.setCurState(EnumActionState.CANCELLED);
			fullActionState.setEndTurn(currentTurn);
		}
			
	}
	
	private void initiateAction(Long currentTurn, ActionInfo fullActionState) {
		
		// Set the start turn to check
		fullActionState.setStartTurn(currentTurn);
		
		// Set the end turn
		if (fullActionState.getActionClass().getActionType()!=ActionClass.ACTION_CLASS_CONTINUOUS) {
			
			if (fullActionState.getActionClass().getNroTurnsExpr()!=null)
				// Calculating the end turn
				fullActionState.setEndTurn(calculateEndTurn(currentTurn, fullActionState));
			else
				// instant action
				fullActionState.setEndTurn(fullActionState.getStartTurn());
		}
	}
	
	private void applyEffects(ActionInfo fullActionState) {
		
		// Calculate successRate
		if (fullActionState.getActionClass().getSuccessRateExpr()!=null) {
			calculateSuccessRate(fullActionState);
		} else {
			fullActionState.setSuccessRate(1.0D);
		}

		// Apply effects
		calculateEffect(fullActionState);
	}
	

	private void updateEntities(ActionInfo fullState) {
		
		// Update the actor
		beingService.updateBeing( 
					fullState.getActor().getBeing().getCode(), 
					fullState.getActor().getBeing());
		
		// Update the place where the actor is
		placeService.updatePlace( 
				fullState.getActor().getPlace().getCode(), 
				fullState.getActor().getPlace());

		
		// If the Mediator is used, updated it too
		if (fullState.getMediator()!=null) {
			itemService.updateItem(fullState.getMediator().getCode(), fullState.getMediator());
		}

		// Updating the target
		switch(fullState.getTargetType()) {
			case BEING: 
				
				BeingComposite targetBeing = (BeingComposite)fullState.getTarget();
				beingService.updateBeing(targetBeing.getBeing().getCode(), targetBeing.getBeing());

				break;
			
			case ITEM: 
				
				Item targetItem = (Item)fullState.getTarget();
				itemService.updateItem(targetItem.getCode(), targetItem);
				
				break;
			
			case PLACE: 
				
				PlaceComposite targetPlace = (PlaceComposite)fullState.getTarget();
				placeService.updatePlace(targetPlace.getPlace().getCode(), targetPlace.getPlace());
				
				break;
			
			case DIRECTION: 
				
				// Do nothing
				break;
			
		}
		
		
	}
	
	private void sendMessages(ActionInfo fullState) {
		
		for(ActionMessage curTargetMessage: fullState.getMessages()) {

			// Send message to the target
			switch (fullState.getTargetType()) {
			
			case BEING:
				this.messageService.putMessage(fullState.getActorCode(), 
						curTargetMessage.getMessageKey(), 
						curTargetMessage.getSenderCode(),
						fullState.getActor().getBeing().getName(),
						curTargetMessage.getArgs());
				break;
			
			case PLACE:
				throw new IllegalParameterException("Messages to PLACEs not supported.");
				
			case ITEM:
				throw new IllegalParameterException("Messages to ITEMs not supported.");
				
			default:
			}
		}
	}

	private void checkPrerequisites(ActionInfo e) {

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);

		for (ActionClassPrereq curPrereq : e.getActionClass().getPrereqList()) {

			// Running prereq expressions
			Expression curExpression = parser.parseExpression(curPrereq.getCheckExpression());
			
			boolean accepted = false;
			
			try {
				// Evaluate the expression
				accepted = curExpression.getValue(context, Boolean.class);
	
			} catch(Exception ex) {
				
				// Do nothing
			}
			
			if (!accepted) {
				
				if (curPrereq.getFailExpression()!=null) {
					
					// Prepare the fail expression
					Expression failExpression = parser.parseExpression(curPrereq.getFailExpression());
					
					// Just evaluate the expression
					failExpression.getValue(context);
				}

				throw new ActionRefusedException(LocalizedMessages.ACTION_REFUSED);
			}
			
		}

	}

	private ActionInfo calculateEffect(ActionInfo e) {

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);

		for (ActionClassEffect curEffect : e.getActionClass().getEffectList()) {

			// Running effect expressions
			Expression curExpression = parser.parseExpression(curEffect.getExpression());

			// Just evaluate the expression
			curExpression.getValue(context);
		}

		return e;
	}

	private ActionInfo calculateSuccessRate(ActionInfo e) {

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);

		// Running successRate expressions
		Expression curExpression = parser.parseExpression(e.getActionClass().getSuccessRateExpr());
		
		try {

			Double successRate = curExpression.getValue(context, Double.class);

			e.setSuccessRate(successRate);
		} catch(Exception ex) {
			
			// Sets the success rate to zero
			e.setSuccessRate(0D);
		}

		return e;
	}

	private Long calculateEndTurn(Long currentTurn, ActionInfo e) {

		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext(e);

		// Running successRate expressions
		Expression curExpression = parser.parseExpression(e.getActionClass().getNroTurnsExpr());

		Long nroTurns = curExpression.getValue(context, Long.class);

		return (nroTurns + currentTurn);
	}	
}
