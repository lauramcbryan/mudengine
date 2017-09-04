package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.action.client.BeingServiceClient;
import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.client.PlaceServiceClient;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHandler;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.action.utils.ActionInfo;
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
	
	@Scheduled(fixedRate=10000)
	public void updateActions() {
		
		System.out.println("ActionScheduler.  Turn=" + ActionScheduler.currentTurn);
		
		// List of processed actors in this iteraction
		List<Long> processedActors = new ArrayList<Long>();
		
		// List of pending actions
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


		// List of pending actions
		List<MudAction> pendingActions = repository.findPendingActions();
		
		for(MudAction curPendingAction: pendingActions) {
			
			// Check if there´s another action running for the same actor
			// That need to be done in the case we already started an action for the same actor in this iteration
			if (processedActors.indexOf(curPendingAction.getActorCode())==-1) {
				
				try {
				
					Action curAction = ActionHelper.buildAction(curPendingAction);
					ActionInfo fullState = buildAction(curAction);
					
					handler.updateAction(getCurrentTurn(), curAction, fullState);
					
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
		
		beingService.updateBeing(authToken, fullState.getActor().getBeingCode(), fullState.getActor());
		
		if (fullState.getMediator()!=null) {
			itemService.updateItem(authToken, fullState.getMediator().getItemCode(), fullState.getMediator());
		}
		
		switch(fullState.getTargetType()) {
			case BEING: {
				
				Being targetBeing = (Being)fullState.getTarget();
				
				beingService.updateBeing(authToken, targetBeing.getBeingCode(), targetBeing);

				break;
			}
			case ITEM: {
				
				Item targetItem = (Item)fullState.getTarget();
				
				itemService.updateItem(authToken, targetItem.getItemCode(), targetItem);
				
				break;
			}
			case PLACE: {
				
				Place targetPlace = (Place)fullState.getTarget();
				
				placeService.updatePlace(targetPlace.getPlaceCode(), targetPlace);
				
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
		result.setActionCode(a.getActionCode());
		
		//Actor
		if (a.getActorCode()!=null) {
			
			Being actor = beingService.getBeing(token, a.getActorCode());
			
			if (actor!=null) {
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
		
		// Place
		if (a.getPlaceCode()!=null) {
			
			Place place = placeService.getPlace(a.getPlaceCode());
			
			if (place!=null) {
				result.setPlace(place);
			} else {
				throw new EntityNotFoundException("Place  " + a.getPlaceCode() + " not found");
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
					target = placeService.getPlace(Integer.valueOf(a.getTargetCode()));
					
					if (target==null) {
						result.setTarget(target);
					} else {
						throw new EntityNotFoundException("Place " + a.getTargetCode() + " not found");
					}
					
					break;
				}
			case BEING: {
					target = beingService.getBeing(token, Long.valueOf(a.getTargetCode()));
					
					if (target==null) {
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
