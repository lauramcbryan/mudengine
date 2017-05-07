package com.jpinfo.mudengine.action.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.common.utils.ServiceCatalog;

@Service
public class ActionScheduler {
	
	private static Long currentTurn = 0L;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private MudActionClassRepository classRepository;
	
	@Autowired
	private RestTemplate restTemplate;

	@Scheduled(fixedRate=60000)
	public void updateActions() {
		
		System.out.println("ActionScheduler.  Turn=" + ActionScheduler.currentTurn);
		
		
		
		updatePendingActions();
		
		//updateActiveActions();
		//updatePendingMessages();
	
		
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
				
				// Check the prerequisites
				ActionState fullActionState = buildAction(curPendingAction);
				
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
				
				//repository.save(curPendingAction);
			} // endif
		} // next pendingAction
		
	}
	
	
	private void updateActiveActions() {
		
		// Obter uma lista das actions Ativas e que estão terminando
		List<MudAction> actionList = repository.findFinishedActions(ActionScheduler.currentTurn);
		
		for(MudAction curAction: actionList) {
			
			// Apply the effects
			ActionState fullActionState = buildAction(curAction);
			
			//fullActionState = calculateEffect(fullActionState);
			
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
	
	private ActionState buildAction(MudAction a) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		ActionState result = new ActionState();

		result.setActionId(a.getActionId());
		result.setActionCode(a.getActionCode());
		
		//Actor

		if (a.getActorCode()!=null) {
			
			List<ServiceInstance> instanceList = discoveryClient.getInstances(ServiceCatalog.MUD_BEING_SERVICE);
			
			System.out.println(instanceList);
			
			
			//Being actor = restTemplate.getForObject(getBeingServiceUrl(), Being.class, a.getActorCode());
			//result.setActor(actor);
		}
		
/*
		// Mediator
		if (a.getMediatorCode()!=null) {
			
			Item item = restTemplate.getForObject(getItemServiceUrl(), Item.class, a.getMediatorCode());
			result.setMediator(item);
		}
		
		// Place
		if (a.getPlaceCode()!=null) {
			
			Place place = restTemplate.getForObject(getPlaceServiceUrl(), Place.class, a.getPlaceCode());
			result.setPlace(place);
		}
		
		if (a.getTargetCode()!=null) {
			
			ActionTarget target = null;
			
			switch(a.getTargetType()) {
			case "ITEM":
				target = restTemplate.getForObject(getItemServiceUrl(), Item.class, a.getTargetCode());
				break;
			case "PLACE":
				target = restTemplate.getForObject(getPlaceServiceUrl(), Place.class, a.getTargetCode());
				break;
			case "BEING":
				target = restTemplate.getForObject(getBeingServiceUrl(), Being.class, a.getTargetCode());
				break;
			case "PLACE_CLASS":
				target = restTemplate.getForObject(getPlaceClassServiceUrl(), PlaceClass.class, a.getTargetCode());
				break;
			}
			
			result.setTarget(target);
		}
		*/
		
		return result;
	}


}
