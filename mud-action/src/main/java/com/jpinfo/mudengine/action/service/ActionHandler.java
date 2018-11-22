package com.jpinfo.mudengine.action.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.jpinfo.mudengine.action.rules.ActionRuleService;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.exception.ActionRefusedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;

@Component
public class ActionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ActionHandler.class);

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
	
	@Autowired
	private ActionRuleService ruleService;
	
	public void runActions(Long currentTurn, List<MudAction> actionList) {
		
		actionList.stream().forEach(curPendingAction -> {
			
			try {
				ActionInfo fullState = actionInfoConverter.build(curPendingAction);
				
				fullState = runOneAction(currentTurn, fullState);
				
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
	
	public ActionInfo runOneAction(Long currentTurn, ActionInfo fullActionState) {
		
		try {
			
			// If we are initiating an action right now, update the fields
			if (!fullActionState.hasInitiated()) {
				initiateAction(currentTurn, fullActionState);
				
				log.info("turn: {}, world: {}, actor: {}, action: {}, status: STARTED, phase: INITIATED",
						currentTurn,
						fullActionState.getActor().getBeing().getCurWorld(),
						fullActionState.getActor().getBeing().getCode(),
						fullActionState.getActionClassCode()
						);
				
				// Update the action state to RUNNING
				fullActionState.setCurState(Action.EnumActionState.STARTED);
			}
			
			// Check prerequisites
			fullActionState = ruleService.prereqCheck(fullActionState.getActionClassCode(), fullActionState);
			
			log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: PREREQ_CHECK",
					currentTurn,
					fullActionState.getActor().getBeing().getCurWorld(),
					fullActionState.getActor().getBeing().getCode(),
					fullActionState.getActionClassCode(),
					fullActionState.getCurState()
					);


			// Apply the effects if needed
			if (fullActionState.shallApplyEffects(currentTurn))
			{
				fullActionState = ruleService.applyEffects(fullActionState.getActionClassCode(), fullActionState);
				
				log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: APPLY_EFFECTS",
						currentTurn,
						fullActionState.getActor().getBeing().getCurWorld(),
						fullActionState.getActor().getBeing().getCode(),
						fullActionState.getActionClassCode(),
						fullActionState.getCurState()
						);
				
				// Update changed entities
				updateEntities(currentTurn, fullActionState);
			}

			// Send the message in the message queue
			sendMessages(currentTurn, fullActionState);

			
			// if reach end_turn, complete it
			if (fullActionState.isFinished(currentTurn)) {

				// Update the action to completed
				fullActionState.setCurState(Action.EnumActionState.COMPLETED);
				
				log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: COMPLETED",
						currentTurn,
						fullActionState.getActor().getBeing().getCurWorld(),
						fullActionState.getActor().getBeing().getCode(),
						fullActionState.getActionClassCode(),
						fullActionState.getCurState()
						);
			}
			
		} catch (ActionRefusedException e) {

			// If the action was not started, update it to REFUSED
			if (!fullActionState.hasInitiated())
				fullActionState.setCurState(Action.EnumActionState.REFUSED);
			else
				// If it was started, update it to COMPLETE
				fullActionState.setCurState(Action.EnumActionState.COMPLETED);
			
			log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: ACTION_REFUSED_EXCEPTION",
					currentTurn,
					fullActionState.getActor().getBeing().getCurWorld(),
					fullActionState.getActor().getBeing().getCode(),
					fullActionState.getActionClassCode(),
					fullActionState.getCurState()
					);
			
			fullActionState.setEndTurn(currentTurn);
			
		} catch(EntityNotFoundException e) {
			
			fullActionState.setCurState(EnumActionState.CANCELLED);
			fullActionState.setEndTurn(currentTurn);
			
			log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: ENTITY_NOT_FOUND_EXCEPTION",
					currentTurn,
					fullActionState.getActor().getBeing().getCurWorld(),
					fullActionState.getActor().getBeing().getCode(),
					fullActionState.getActionClassCode(),
					fullActionState.getCurState()
					);
			
		}
		
		return fullActionState;
			
	}
	
	private void initiateAction(Long currentTurn, ActionInfo fullActionState) {
		
		// Set the start turn to check
		fullActionState.setStartTurn(currentTurn);
		
		// Set the end turn
		switch(fullActionState.getRunType()) {
		case CONTINUOUS:
			break;
		case PROLONGED:
			Integer nroTurns = ruleService.calculateTurns(fullActionState.getActionClassCode(), fullActionState);
			
			fullActionState.setEndTurn(currentTurn + nroTurns);
			break;
		case SIMPLE:
			fullActionState.setEndTurn(currentTurn+1);
			break;
		default:
			break;
		
		}
	}
	

	private void updateEntities(Long currentTurn, ActionInfo fullState) {
		
		// Update the actor
		log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: UPDATE_ACTOR",
				currentTurn,
				fullState.getActor().getBeing().getCurWorld(),
				fullState.getActor().getBeing().getCode(),
				fullState.getActionClassCode(),
				fullState.getCurState()
				);
		
		beingService.updateBeing( 
					fullState.getActor().getBeing().getCode(), 
					fullState.getActor().getBeing());
		
		// Update the place where the actor is
		log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: UPDATE_PLACE",
				currentTurn,
				fullState.getActor().getBeing().getCurWorld(),
				fullState.getActor().getBeing().getCode(),
				fullState.getActionClassCode(),
				fullState.getCurState()
				);
		
		placeService.updatePlace( 
				fullState.getActor().getPlace().getCode(), 
				fullState.getActor().getPlace());

		
		// If the Mediator is used, update it too
		if (fullState.getMediator()!=null) {
			log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: UPDATE_MEDIATOR",
					currentTurn,
					fullState.getActor().getBeing().getCurWorld(),
					fullState.getActor().getBeing().getCode(),
					fullState.getActionClassCode(),
					fullState.getCurState()
					);

			itemService.updateItem(fullState.getMediator().getCode(), fullState.getMediator());
		}

		// Updating the target
		log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: UPDATE_TARGET",
				currentTurn,
				fullState.getActor().getBeing().getCurWorld(),
				fullState.getActor().getBeing().getCode(),
				fullState.getActionClassCode(),
				fullState.getCurState()
				);
		
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
	
	private void sendMessages(Long currentTurn, ActionInfo fullState) {
		
		for(ActionMessage curTargetMessage: fullState.getMessages()) {

			log.info("turn: {}, world: {}, actor: {}, action: {}, status:{}, phase: SEND_MESSAGE",
					currentTurn,
					fullState.getActor().getBeing().getCurWorld(),
					fullState.getActor().getBeing().getCode(),
					fullState.getActionClassCode(),
					fullState.getCurState()
					);
			
			// Send message to the target
			switch (curTargetMessage.getTargetType()) {
			
			case BEING:
				this.messageService.putMessage(
						curTargetMessage.getTargetCode(),
						curTargetMessage);
				break;
			
			case PLACE:
				this.messageService.broadcastMessage(
						curTargetMessage.getTargetCode().intValue(),
						curTargetMessage
						);
				
				break;
				
			case ITEM:
				throw new IllegalParameterException("Messages to ITEMs not supported.");
				
			default:
			}
		}
	}
}
