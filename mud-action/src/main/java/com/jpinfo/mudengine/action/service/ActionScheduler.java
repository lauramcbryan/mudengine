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

import com.jpinfo.mudengine.action.exception.ActionRefusedException;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCost;
import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.ActionSimpleState;
import com.jpinfo.mudengine.common.action.ActionState;

@Service
public class ActionScheduler {
	
	private static Long currentTurn = 0L;
	
	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private MudActionClassRepository classRepository;

	@Scheduled(fixedRate=10000)
	public void updateActions() {
		
		updatePendingActions();
		
		updateActiveActions();
		updatePendingMessages();
	
		
		currentTurn++;		
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
			MudAction dummy = repository.findFirstOneByCurStateAndActorCode(ActionSimpleState.STARTED, curPendingAction.getActorCode());
			
			if (dummy==null) {
				
				// Check the prerequisites
				ActionState fullActionState = ActionHelper.buildAction(curPendingAction);
				
				try {
					checkPrerequisites(fullActionState);
					
					// Update the action to Started
					curPendingAction.setCurrState(ActionSimpleState.STARTED);
					curPendingAction.setStartTurn(ActionScheduler.currentTurn);
					
					// Calculates the endTurn (it´s included in costs)
					fullActionState = calculateCost(fullActionState);
					
					// After this, the endTurn is set in action object
					curPendingAction.setEndTurn(fullActionState.getEndTurn());
					
				} catch (ActionRefusedException e) {
					
					// Update the action to Refused
					curPendingAction.setCurrState(ActionSimpleState.REFUSED);
					curPendingAction.setStartTurn(ActionScheduler.currentTurn);
					curPendingAction.setEndTurn(ActionScheduler.currentTurn);
				}
				
				repository.save(curPendingAction);
			} // endif
		} // next pendingAction
		
	}
	
	
	private void updateActiveActions() {
		
		// Obter uma lista das actions Ativas e que estão terminando
		List<MudAction> actionList = repository.findFinishedActions(ActionScheduler.currentTurn);
		
		for(MudAction curAction: actionList) {
			
			// Apply the effects
			ActionState fullActionState = ActionHelper.buildAction(curAction);
			
			fullActionState = calculateEffect(fullActionState);
			
			// TODO: Update changed entities
			
			
			// Update the action to COMPLETED
			curAction.setCurrState(ActionSimpleState.COMPLETED);
			
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
			
		}
		
		return e;
	}

}
