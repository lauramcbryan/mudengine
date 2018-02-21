package com.jpinfo.mudengine.action.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.dto.BeingComposite;
import com.jpinfo.mudengine.action.dto.ItemComposite;
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.action.exception.ActionRefusedException;
import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.action.ActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;

@Component
public class ActionHandler {
	
	@Autowired
	private MudActionClassRepository classRepository;	
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceServiceClient placeService;
	
	@Autowired
	private ItemServiceClient itemService;
	

	public void updateAction(Long currentTurn, Action curAction, ActionInfo fullActionState) {

		switch (curAction.getCurState()) {

		case NOT_STARTED: {

			try {

				// Set the start turn to check
				curAction.setStartTurn(currentTurn);
				
				// Variable to hold the future state of the action
				Action.EnumActionState futureState = Action.EnumActionState.STARTED;

				// Check the prerequisites
				checkPrerequisites(fullActionState);

				// Set the end turn (except for continuous actions)
				if (fullActionState.getActionClass().getActionType()!=ActionClass.ACTION_CLASS_CONTINUOUS) {
					
					// Set the end turn
					if (fullActionState.getActionClass().getNroTurnsExpr() != null) {
						
						curAction.setEndTurn(calculateEndTurn(currentTurn, fullActionState));

					} else {
						
						// If not specified, the endTurn is the same as the initial one (instant action)
						curAction.setEndTurn(curAction.getStartTurn());
						
						// Calculate successRate
						if (fullActionState.getActionClass().getSuccessRateExpr()!=null) {
							fullActionState = calculateSuccessRate(fullActionState);
						} else {
							fullActionState.setSuccessRate(1.0D);
						}

						// Reapply effects
						fullActionState = calculateEffect(fullActionState);
						
						// As it is a instant action, it goes straight to COMPLETED status
						futureState = Action.EnumActionState.COMPLETED;
						
					}
				} 

				// Update the action to Started
				curAction.setCurState(futureState);

			} catch (ActionRefusedException e) {

				// Update the action to Refused
				curAction.setCurState(Action.EnumActionState.REFUSED);
				curAction.setEndTurn(currentTurn);
			}

			break;
		}
		case STARTED: {

			try {
				// Recheck prerequisites
				checkPrerequisites(fullActionState);

				// If the action is continuous, reapply the effects

				if ((fullActionState.getActionClass().getActionType().equals(ActionClass.ACTION_CLASS_CONTINUOUS)) ||
					(currentTurn >=curAction.getEndTurn()))
				{

					// Calculate successRate
					if (fullActionState.getActionClass().getSuccessRateExpr()!=null) {
						fullActionState = calculateSuccessRate(fullActionState);
					} else {
						fullActionState.setSuccessRate(1.0D);
					}

					// Reapply effects
					fullActionState = calculateEffect(fullActionState);
				}

				// if reach end_turn, complete it
				if ((curAction.getEndTurn() != null) && (curAction.getEndTurn()) <= currentTurn) {

					// Update the effect to completed
					curAction.setCurState(Action.EnumActionState.COMPLETED);
				}

			} catch (ActionRefusedException e) {

				// Update the action to Refused
				curAction.setCurState(Action.EnumActionState.COMPLETED);
				curAction.setEndTurn(currentTurn);
			}

			break;
		}
		default:
		}
	}

	private void checkPrerequisites(ActionInfo e) throws ActionRefusedException {

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

				throw new ActionRefusedException();
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
	
	public ActionInfo buildActionInfo(Action a) throws EntityNotFoundException {
		
		ActionInfo result = new ActionInfo();
		
		result.setActionId(a.getActionId());
		result.setActionClassCode(a.getActionClassCode());
		
		// Solving the actionClass
		MudActionClass dbActionClass = classRepository.findOne(a.getActionClassCode());
		result.setActionClass(ActionHelper.buildActionClass(dbActionClass));

		// Preparing the token to internally call the services to compose the ActionInfo
		String token = TokenService.buildInternalToken();

		
		//Actor
		if (a.getActorCode()!=null) {
			
			Being actorBeing = beingService.getBeing(token, a.getActorCode());
			
			if (actorBeing!=null) {
				
				// Assemble the composite
				BeingComposite actor = new BeingComposite(actorBeing);
				
				// Set the place
				Place curPlace = placeService.getPlace(token, actorBeing.getCurPlaceCode());
				actor.setPlace(curPlace);
				
				result.setActor(actor);
				
			} else {
				throw new EntityNotFoundException("Being " + a.getActorCode() + " not found");
			}
		}
		
		// Mediator
		if (a.getMediatorCode()!=null) {
			
			switch(a.getMediatorType()) {
			
			case ITEM: {
				Item mediator = itemService.getItem(token, Long.valueOf(a.getMediatorCode()));
				
				if (mediator!=null) {
					result.setMediator(mediator);
				} else {
					throw new EntityNotFoundException("Item " + a.getMediatorCode() + " not found");
				}
				
				break;
			}
			
			case PLACE:
				throw new IllegalParameterException("PLACE Mediators not supported.");
				
			case BEING:
				throw new IllegalParameterException("BEING Mediators not supported.");
			
			default:
			}
			
		}
		
		if (a.getTargetCode()!=null) {
			
			ActionTarget target = null;
			
			switch(a.getTargetType()) {
			case ITEM: {
				
					Item targetItem = itemService.getItem(token, Long.valueOf(a.getTargetCode()));
					
					if (targetItem!=null) {
						target = new ItemComposite(targetItem);
					} else {
						throw new EntityNotFoundException("Item " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case PLACE: {
				
					Place targetPlace = placeService.getPlace(token, Integer.valueOf(a.getTargetCode()));
				
					if (targetPlace!=null) {
						target = new PlaceComposite(targetPlace);
					} else {
						throw new EntityNotFoundException("Place " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case BEING: {
					Being targetBeing = beingService.getBeing(token, Long.valueOf(a.getTargetCode()));
					
					if (targetBeing!=null) {
						target = new BeingComposite(targetBeing);
					} else {
						throw new EntityNotFoundException("Being " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case DIRECTION:
			default: {
				result.setTargetCode(a.getTargetCode());
				}
			}
			
			if (target!=null) {
				result.setTarget(target);
			}
		}
		
		return result;
	}	
}
