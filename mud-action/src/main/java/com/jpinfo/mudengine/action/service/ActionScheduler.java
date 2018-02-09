package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.dto.BeingComposite;
import com.jpinfo.mudengine.action.dto.PlaceComposite;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHandler;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionMessages;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumActionState;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
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
	private ActionHandler handler;
	
	@Profile("!unitTest")
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
					ActionInfo fullState = buildAction(curAction);
					
					handler.updateAction(getCurrentTurn(), curAction, fullState);
					
					//curPendingAction.setSuccessRate(curAction.gets);
					curRunningAction.setCurrState(curAction.getCurState());
					
					// Update changed entities
					updateEntities(fullState);
					
					// Update message queue
					updateMessageQueue(fullState);
					
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
					ActionInfo fullState = buildAction(curAction);
					
					handler.updateAction(getCurrentTurn(), curAction, fullState);

					// TODO: Evaluate successRate
					//curPendingAction.setSuccessRate(curAction.gets);
					curPendingAction.setCurrState(curAction.getCurState());
					curPendingAction.setStartTurn(curAction.getStartTurn());
					curPendingAction.setEndTurn(curAction.getEndTurn());
					
					// Update message queue
					updateMessageQueue(fullState);
					
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
	
	public void updateEntities(ActionInfo fullState) {
		
		String authToken = TokenService.buildInternalToken();

		// Update the actor
		beingService.updateBeing(authToken, 
					fullState.getActor().getBeing().getBeingCode(), 
					fullState.getActor().getBeing());

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
				
				placeService.updatePlace(targetPlace.getPlace().getPlaceCode(), targetPlace.getPlace());
				
				break;
			}
		}
		
		
	}
	
	public void updateMessageQueue(ActionInfo fullState) {
		
		for(ActionMessages curMessage: fullState.getMessages()) {
			
			if (curMessage.getPlainMessage()!=null) {
				
				// TODO: Send the message 
				
				
			} else {
				// TODO: Send the message				
			}
			
		}
	}
	
	public static Long getCurrentTurn() {
		return ActionScheduler.currentTurn;
	}
	
	private ActionInfo buildAction(Action a) throws EntityNotFoundException {
		
		ActionInfo result = new ActionInfo();
		
		String token = TokenService.buildInternalToken();

		result.setActionId(a.getActionId());
		result.setActionClassCode(a.getActionClassCode());
		
		//Actor
		if (a.getActorCode()!=null) {
			
			BeingComposite actor = new BeingComposite(beingService.getBeing(token, a.getActorCode()));
			
			if (actor.getBeing()!=null) {
				
				// Assemble the composite
				
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
					target = new PlaceComposite(placeService.getPlace(Integer.valueOf(a.getTargetCode())));
					
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
			}
			
			if (target!=null) {
				result.setTarget(target);
			}
		}
		
		return result;
	}

}
