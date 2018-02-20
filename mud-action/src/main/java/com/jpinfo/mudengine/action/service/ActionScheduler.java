package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.MessageServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.dto.BeingComposite;
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.security.TokenService;

@Service
public class ActionScheduler {
	
	private static Long currentTurn = 0L;
	
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
	private ActionHandler handler;
	
	@Profile("!default")
	@Scheduled(fixedRate=10000)
	public void updateActions() {
		
		System.out.println("ActionScheduler.  Turn=" + ActionScheduler.currentTurn);
		
		// List of processed actors in this iteraction
		List<Long> processedActors = new ArrayList<Long>();
		
		// Get the list of running actions and update them.
		List<MudAction> runningActions = repository.findRunningActions(getCurrentTurn());
		
		for(MudAction curRunningAction: runningActions) {
			
			// Check if there´s another action running for the same actor
			// That need to be done in the case we already started an action for the same actor in this iteration
			if (processedActors.indexOf(curRunningAction.getActorCode())==-1) {
				
				try {
				
					Action curAction = ActionHelper.buildAction(curRunningAction);
					ActionInfo fullState = handler.buildAction(curAction);
					
					handler.updateAction(getCurrentTurn(), curAction, fullState);
					
					curRunningAction.setCurrState(curAction.getCurState());
					curRunningAction.setEndTurn(curAction.getEndTurn());
					
					// Update changed entities
					updateEntities(fullState);
					
					// Update message queue
					sendMessages(fullState);
					
				} catch(EntityNotFoundException e) {
					curRunningAction.setCurrState(EnumActionState.CANCELLED);
					curRunningAction.setEndTurn(getCurrentTurn());
				}
				
				repository.save(curRunningAction);

				processedActors.add(curRunningAction.getActorCode());
			} // endif
		} // next runningAction


		// Retrieve the list of pending (not started) actions and start them.
		List<MudAction> pendingActions = repository.findPendingActions();
		
		for(MudAction curPendingAction: pendingActions) {
			
			// Check if there´s another action running for the same actor
			// That need to be done in the case we already started an action for the same actor in this iteration
			if (processedActors.indexOf(curPendingAction.getActorCode())==-1) {
				
				try {
				
					Action curAction = ActionHelper.buildAction(curPendingAction);
					ActionInfo fullState = handler.buildAction(curAction);
					
					handler.updateAction(getCurrentTurn(), curAction, fullState);

					curPendingAction.setCurrState(curAction.getCurState());
					curPendingAction.setStartTurn(curAction.getStartTurn());
					curPendingAction.setEndTurn(curAction.getEndTurn());
					
					// Update message queue
					sendMessages(fullState);
					
				} catch(EntityNotFoundException e) {
					curPendingAction.setCurrState(EnumActionState.CANCELLED);
					curPendingAction.setEndTurn(getCurrentTurn());
				}
				
				repository.save(curPendingAction);

				processedActors.add(curPendingAction.getActorCode());
			} // endif
		} // next pendingAction
				
		
		ActionScheduler.currentTurn++;
	}
	
	private void updateEntities(ActionInfo fullState) {
		
		String authToken = TokenService.buildInternalToken();

		// Update the actor
		beingService.updateBeing(authToken, 
					fullState.getActor().getBeing().getBeingCode(), 
					fullState.getActor().getBeing());
		
		// Update the place where the actor is
		placeService.updatePlace(authToken, 
				fullState.getActor().getPlace().getPlaceCode(), 
				fullState.getActor().getPlace());

		
		// If the Mediator is used, updated it too
		if (fullState.getMediator()!=null) {
			itemService.updateItem(authToken, fullState.getMediator().getItemCode(), fullState.getMediator());
		}

		// Updating the target
		switch(fullState.getTargetType()) {
			case BEING: {
				
				BeingComposite targetBeing = (BeingComposite)fullState.getTarget();
				
				beingService.updateBeing(authToken, targetBeing.getBeing().getBeingCode(), targetBeing.getBeing());

				break;
			}
			case ITEM: {
				
				Item targetItem = (Item)fullState.getTarget();
				
				itemService.updateItem(authToken, targetItem.getItemCode(), targetItem);
				
				break;
			}
			case PLACE: {
				
				PlaceComposite targetPlace = (PlaceComposite)fullState.getTarget();
				
				placeService.updatePlace(authToken, targetPlace.getPlace().getPlaceCode(), targetPlace.getPlace());
				
				break;
			}
			case DIRECTION: {
				
				// Do nothing
				break;
			}
		}
		
		
	}
	
	private void sendMessages(ActionInfo fullState) {
		
		String authToken = TokenService.buildInternalToken();
		
		for(ActionMessage curActorMessage : fullState.getActor().getMessages()) {
			
			// Send message to the actor
			this.messageService.putMessage(authToken, fullState.getActorCode(), 
					curActorMessage.getMessageKey(), 
					null, null,
					curActorMessage.args);
			
		}
		
		for(ActionMessage curTargetMessage: fullState.getTarget().getMessages()) {

			// Send message to the target
			switch (fullState.getTargetType()) {
			
			case BEING:
				this.messageService.putMessage(authToken, fullState.getActorCode(), 
						curTargetMessage.getMessageKey(), 
						fullState.getActor().getBeing().getBeingCode(), fullState.getActor().getBeing().getName(),
						curTargetMessage.args);
				break;
			
			case PLACE:
				throw new IllegalParameterException("Messages to PLACEs not supported.");
				
			case ITEM:
				throw new IllegalParameterException("Messages to ITEMs not supported.");
				
			default:
			}
		}
	}
	
	public static Long getCurrentTurn() {
		return ActionScheduler.currentTurn;
	}

}
