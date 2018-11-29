package com.jpinfo.mudengine.item.notification;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.repository.ItemRepository;

@Component
public class NotificationListener {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
	
	@Autowired
	private ItemRepository repository;
	
	@Autowired
	private TokenService tokenService;

	@JmsListener(destination="${place.topic}")
	@JmsListener(destination="${being.topic}")
	public void receiveNotification(
			@Header(name=CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@Payload NotificationMessage msg) {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(authToken)
				);

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
			
			log.info("world: {}, entityId: {}, event: PLACE_DESTROY received",
					msg.getWorldName(), msg.getEntityId());
			
			
			// Destroy all the items in the same place
			repository.findByCurWorldAndCurPlaceCode(
					msg.getWorldName(), 
					msg.getEntityId().intValue())
				.stream()
				.forEach(curItem -> { 
					repository.delete(curItem);
					
					log.info("world: {}, entityId: {}, itemId: {} deleted",
							msg.getWorldName(), msg.getEntityId(), curItem.getCode());
				});
		} else {
			log.trace("world: {}, entityId: {}, event: {} ignored",
					msg.getWorldName(), msg.getEntityId(), msg.getEvent());
		}
	}
	
	private void handleBeingNotification(NotificationMessage msg) {
		
		if (msg.getEvent().equals(NotificationMessage.EnumNotificationEvent.BEING_DESTROY)) {
			
			log.info("world: {}, entityId: {}, event: BEING_DESTROY received",
					msg.getWorldName(), msg.getEntityId());
			
			repository.findByCurOwner(msg.getEntityId())
				.stream()
				.forEach(curItem -> {
					
					curItem.setCurOwner(null);
					curItem.setCurWorld(msg.getWorldName());
					curItem.setCurPlaceCode(msg.getTargetEntityId().intValue());
					
					repository.save(curItem);
					
					log.info("world: {}, entityId: {}, itemId: {} dropped",
							msg.getWorldName(), msg.getEntityId(), curItem.getCode());
				});
			
		} else {
			log.trace("world: {}, entityId: {}, event: {} ignored",
					msg.getWorldName(), msg.getEntityId(), msg.getEvent());
		}
	}

}
