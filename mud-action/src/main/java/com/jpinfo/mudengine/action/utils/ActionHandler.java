package com.jpinfo.mudengine.action.utils;

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
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.action.exception.ActionRefusedException;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.action.ActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
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
					}
				}

				// Update the action to Started
				curAction.setCurState(Action.EnumActionState.STARTED);

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
			Expression curExpression = parser.parseExpression(curPrereq.getExpression());
			
			boolean accepted = false;
			
			try {

				accepted = curExpression.getValue(context, Boolean.class);
	
			} catch(Exception ex) {
				
				// Do nothing
			}
			
			if (!accepted) {
				
				// TODO: Update the message queue
				// e.sendMessageTo(e.getActorCode(), Action.EnumTargetType.BEING,
				// curPrereq.getMessageCode(), new Object[] {});

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

			// TODO: Update the message queue

			// TODO: Mark the changed entities for update

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
		Expression curExpression = parser.parseExpression(e.getActionClass().getSuccessRateExpr());

		Long nroTurns = curExpression.getValue(context, Long.class);

		return (nroTurns + currentTurn);
	}
	
	public ActionInfo buildAction(Action a) throws EntityNotFoundException {
		
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
			
			BeingComposite actor = new BeingComposite(beingService.getBeing(token, a.getActorCode()));
			
			if (actor.getBeing()!=null) {
				
				// Assemble the composite
				
				// Set the place
				Place curPlace = placeService.getPlace(token, actor.getBeing().getCurPlaceCode());
				actor.setPlace(curPlace);
				
				result.setActor(actor);
				
			} else {
				throw new EntityNotFoundException("Being " + a.getActorCode() + " not found");
			}
		}
		
		// Mediator
		if (a.getMediatorCode()!=null) {
			
			Item mediator = itemService.getItem(token, a.getMediatorCode());
			
			if (mediator!=null) {
				result.setMediator(mediator);
			} else {
				throw new EntityNotFoundException("Item " + a.getMediatorCode() + " not found");
			}
		}
		
		if (a.getTargetCode()!=null) {
			
			ActionTarget target = null;
			
			switch(a.getTargetType()) {
			case ITEM: {
					target = itemService.getItem(token, Long.valueOf(a.getTargetCode()));
					
					if (target!=null) {
						result.setTarget(target);
					} else {
						throw new EntityNotFoundException("Item " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case PLACE: {
					target = new PlaceComposite(placeService.getPlace(token, Integer.valueOf(a.getTargetCode())));
					
					if (((PlaceComposite)target).getPlace()!=null) {
						result.setTarget(target);
					} else {
						throw new EntityNotFoundException("Place " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case BEING: {
					target = new BeingComposite(beingService.getBeing(token, Long.valueOf(a.getTargetCode())));
					
					if (((BeingComposite)target).getBeing()!=null) {
						result.setTarget(target);
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
	
	
	/*
	private ActionInfo calculateReactions(ActionInfo e, boolean isBefore) {

		EvaluationContext context = new StandardEvaluationContext(e);

		// TODO: Apply effects caused by actor
		for (Reaction curReaction : e.getActor().getReactions(e.getActionClassCode(), isBefore)) {
			applyReaction(context, curReaction);
		}

		// Items of being:
		// It´s disabled by now because I should have all being items retrieved at this
		// time to retrieve their reactions
		// and that could be expensive.
		/*
		 * for(BeingItem curItem: e.getActor().getItems().values()) {
		 * 
		 * for(Reaction curReaction: curItem.getReactions(e.getActionCode(), isBefore))
		 * {
		 * 
		 * applyReaction(context, curReaction); } }


		// TODO: Apply effects caused by target
		for (Reaction curReaction : e.getTarget().getReactions(e.getActionClassCode(), isBefore)) {
			applyReaction(context, curReaction);
		}

		// TODO: Apply effects caused by mediator (if present)
		if (e.getMediator() != null) {

			for (Reaction curReaction : e.getMediator().getReactions(e.getActionClassCode(), isBefore)) {
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
	
	*/

}
