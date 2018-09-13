package com.jpinfo.mudengine.item.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.repository.ItemRepository;

@Component
public class NotificationListener {
	
	@Autowired
	private ItemRepository repository;

	public void receiveNotification(NotificationMessage msg) {

		switch(msg.getEntity()) {
		case ITEM:
			break;
		case PLACE:
			handlePlaceNotification(msg);
			break;
		case BEING:
			handleBeingNotification(msg);
			break;
		default:
			break;
		
		}
		
	}
	
	private void handlePlaceNotification(NotificationMessage msg) {

		if (msg.getEvent().equals(NotificationMessage.EnumNotificationEvent.PLACE_DESTROY)) {
			
			// Destroy all the items in the same place
			repository.findByCurWorldAndCurPlaceCode(
					msg.getWorldName(), 
					msg.getEntityId().intValue())
				.stream()
				.forEach(curItem -> 
					repository.delete(curItem)
				);
		}
	}
	
	private void handleBeingNotification(NotificationMessage msg) {
		
		if (msg.getEvent().equals(NotificationMessage.EnumNotificationEvent.BEING_DESTROY)) {
			
			repository.findByCurOwner(msg.getEntityId())
				.stream()
				.forEach(curItem -> {
					
					msg.getWorldName();
					msg.getTargetEntityId();
					
				});
			
		}
	}

}
