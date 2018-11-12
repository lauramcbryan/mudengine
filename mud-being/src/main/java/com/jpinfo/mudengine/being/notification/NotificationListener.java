package com.jpinfo.mudengine.being.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

@Component
public class NotificationListener {
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private MessageServiceClient messageService;


	public void receiveNotification(NotificationMessage msg) {

		switch(msg.getEntity()) {
		case ITEM:
			handleItemNotification(msg);
			break;
		case PLACE:
			handlePlaceNotification(msg);
			break;
		case BEING:
			// This listener isn't supposed to handle being notifications,
			// since being effects was already made in the original operation 
			// (or in NotificationAspect, where it was detected)
			// Therefore, being events are just ignored here.
		default:
			break;
		
		}
		
	}

	
	private void handlePlaceNotification(NotificationMessage msg) {

		// Gathering all beings in the place
		List<MudBeing> beingListInPlace =
				repository.findByCurWorldAndCurPlaceCode(
						msg.getWorldName(), 
						msg.getEntityId().intValue());
		
		
		switch(msg.getEvent()) {
			case PLACE_ATTR_CHANGE:
				// FUTURE: Apply the changes in all beings in the same place
				break;
			case PLACE_EXIT_CREATE:
			case PLACE_EXIT_DESTROY:
			case PLACE_EXIT_OPEN:
			case PLACE_EXIT_CLOSE:
			case PLACE_EXIT_UNLOCK:
			case PLACE_EXIT_LOCK:
			case PLACE_CLASS_CHANGE:

				// Send a message to all beings in the same place
				beingListInPlace.stream()
					.forEach(curBeing -> 

						messageService.putMessage(curBeing.getCode(), 
								msg.getMessageKey(), msg.getArgs())
						
					);
				
				break;
			case PLACE_DESTROY:

				// Destroy all the beings in the same place
				beingListInPlace.stream()
					.forEach(curBeing -> 
						
						// (That will trigger a BEING_DESTROY notification)
						repository.delete(curBeing)
					);
				
				break;
				
			default:
				break;
		
		}
		
	}
	
	private void handleItemNotification(NotificationMessage msg) {

		// Gathering the being performing the action (if it exists)
		Optional<MudBeing> optActingBeing;

		if (msg.getTargetEntity().equals(NotificationMessage.EnumEntity.BEING)) {
			optActingBeing = repository.findById(msg.getTargetEntityId());
		} else {
			optActingBeing = Optional.empty();
		}

		

		switch(msg.getEvent()) {
			case ITEM_QTTY_INCREASE:
			case ITEM_QTTY_DECREASE:
			case ITEM_ATTR_CHANGE:
			case ITEM_DESTROY:
			case ITEM_CLASS_CHANGE:

				// Check where the item have an owner
				if (optActingBeing.isPresent() && optActingBeing.get().getPlayerId()!=null) {
					
					// Send a message to the being owning the item
					messageService.putMessage(msg.getTargetEntityId(), 
							msg.getMessageKey(), msg.getArgs());
				} else {

					// Gathering all beings in the same place
					repository.findByCurWorldAndCurPlaceCode(
							msg.getWorldName(), 
							msg.getTargetEntityId().intValue())
						.stream()
						.forEach(curBeing -> 

						// Send a message to all other beings in the same place
						messageService.putMessage(curBeing.getCode(), 
								msg.getMessageKey(), msg.getArgs())
						);
				}
				
				break;
			case ITEM_DROP:
	
				// Get the being performing the action (the one who drop the item)
				optActingBeing
					.ifPresent(actingBeing -> {
					
						if (actingBeing.getPlayerId()!=null) {
							// Send a message to the being dropping the item (if it's a playable being)
							messageService.putMessage(msg.getTargetEntityId(), 
									BeingHelper.BEING_DROP_YOURS_MSG, msg.getArgs());
						}
					
						// Gathering all beings in the same place than the acting being
						repository.findByCurWorldAndCurPlaceCode(
										actingBeing.getCurWorld(), 
										actingBeing.getCurPlaceCode())
							.stream()
							// excluding the target being from this list
							.filter(curBeing -> !curBeing.getCode().equals(actingBeing.getCode()))
							// excluding non-playable beings
							.filter(curBeing -> curBeing.getPlayerId()!=null)
							.forEach(curBeing -> 
	
								// Send a message to all other beings in the same place
								messageService.putMessage(curBeing.getCode(), 
										BeingHelper.BEING_DROP_ANOTHER_MSG, msg.getArgs())
							);
					});
				
				break;
				
			case ITEM_TAKEN:
				
				optActingBeing.ifPresent(actingBeing -> {
					
					if (actingBeing.getPlayerId()!=null) {
						// Send a message to the being owning the item
						messageService.putMessage(msg.getTargetEntityId(), 
								BeingHelper.BEING_TAKE_YOURS_MSG, msg.getArgs());
					}
				});
				
				
				break;
				
			default:
				break;
		
		}
		
	}
	
}
