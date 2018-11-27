package com.jpinfo.mudengine.being.notification;

import java.util.List;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

@Component
public class NotificationListener {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private MessageServiceClient messageService;
	
	@Autowired
	private TokenService tokenService;
	
	
	@RabbitListener(queues = {"being.queue"})
	public void receiveNotification(
			@Header(name=CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@Payload NotificationMessage msg) {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(authToken)
				);

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
					.forEach(curBeing -> {
						
						MessageRequest request = new MessageRequest();
						
						request.setMessageKey(msg.getMessageKey());
						request.setArgs(msg.getArgs());
						
						// Adding the place in the changed list
						request.addChangedEntity(EnumEntityType.PLACE, msg.getEntityId()); 

						messageService.putMessage(curBeing.getCode(), request);
						
						log.info("world: {}, entityId: {}, message: {} ",
								msg.getWorldName(), curBeing.getCode(), msg.getMessageKey());
						
					});
				
				break;
			case PLACE_DESTROY:

				// Destroy all the beings in the same place
				beingListInPlace.stream()
					.forEach(curBeing -> {
						
						// (That will trigger a BEING_DESTROY notification)
						repository.delete(curBeing);
						
						log.info("world: {}, entityId: {}, DESTROYED", msg.getWorldName(), curBeing.getCode());
					});
				
				break;
				
			default:
				break;
		
		}
		
	}
	
	private void handleItemNotification(NotificationMessage notification) {

		// Gathering the being performing the action (if it exists)
		Optional<MudBeing> optActingBeing;

		if (notification.getTargetEntity().equals(NotificationMessage.EnumEntity.BEING)) {
			optActingBeing = repository.findById(notification.getTargetEntityId());
		} else {
			optActingBeing = Optional.empty();
		}

		// Preparing the message request
		MessageRequest userMessage = new MessageRequest();
		
		userMessage.setArgs(notification.getArgs());
		
		// Adding the item in the changed list
		userMessage.addChangedEntity(EnumEntityType.ITEM, notification.getEntityId()); 
		
		

		switch(notification.getEvent()) {
			case ITEM_QTTY_INCREASE:
			case ITEM_QTTY_DECREASE:
			case ITEM_ATTR_CHANGE:
			case ITEM_DESTROY:
			case ITEM_CLASS_CHANGE:

				userMessage.setMessageKey(notification.getMessageKey());

				// Check where the item have an owner
				if (optActingBeing.isPresent() && optActingBeing.get().getPlayerId()!=null) {
					
					// Send a message to the being owning the item
					messageService.putMessage(optActingBeing.get().getCode(), userMessage);
					
					log.info("world: {}, entityId: {} (owner), message: {}",
							notification.getWorldName(), optActingBeing.get().getCode(), notification.getMessageKey());
				} else {

					// Gathering all beings in the same place
					repository.findByCurWorldAndCurPlaceCode(
							notification.getWorldName(), 
							notification.getTargetEntityId().intValue())
						.stream()
						.forEach(curBeing -> {

						// Send a message to all beings in the same place
						messageService.putMessage(curBeing.getCode(), userMessage);
						
						log.info("world: {}, entityId: {}, message: {}  ",
								notification.getWorldName(), curBeing.getCode(), notification.getMessageKey());
						});
				}
				
				break;
			case ITEM_DROP:
	
				// Get the being performing the action (the one who drop the item)
				optActingBeing
					.ifPresent(actingBeing -> {
						
						// Send a message to the being dropping the item (if it's a playable being)					
						if (actingBeing.getPlayerId()!=null) {

							// Setting the message to owner
							userMessage.setMessageKey(BeingHelper.BEING_DROP_YOURS_MSG);
							userMessage.setArgs(notification.getArgs());
							
							messageService.putMessage(actingBeing.getCode(), userMessage);
							
							log.info("world: {}, entityId: {} (owner), message: {}",
									actingBeing.getCurWorld(), actingBeing.getCode(), userMessage.getMessageKey());
						}
						
						// Preparing the message for other beings						
						MessageRequest otherMessage = new MessageRequest();
						
						otherMessage.setMessageKey(BeingHelper.BEING_DROP_ANOTHER_MSG);
						otherMessage.setArgs(new String[] {
								actingBeing.getName(),
								notification.getArgs()[0]
						});
						
						// Adding the item in the changed list
						otherMessage.addChangedEntity(EnumEntityType.ITEM, notification.getEntityId()); 
						
					
						// Gathering all beings in the same place than the acting being
						repository.findByCurWorldAndCurPlaceCode(
										actingBeing.getCurWorld(), 
										actingBeing.getCurPlaceCode())
							.stream()
							// excluding the target being from this list
							.filter(curBeing -> !curBeing.getCode().equals(actingBeing.getCode()))
							// excluding non-playable beings
							.filter(curBeing -> curBeing.getPlayerId()!=null)
							.forEach(curBeing -> {
								
								// Send a message to all other beings in the same place
								messageService.putMessage(curBeing.getCode(), otherMessage);
								
								log.info("world: {}, entityId: {}, message: {}",
										curBeing.getCurWorld(), curBeing.getCode(), otherMessage.getMessageKey());
								});
						});
				
				break;
				
			case ITEM_TAKEN:
				
				optActingBeing.ifPresent(actingBeing -> {
					
					if (actingBeing.getPlayerId()!=null) {
						
						userMessage.setMessageKey(BeingHelper.BEING_TAKE_YOURS_MSG);
						
						// Send a message to the being owning the item
						messageService.putMessage(actingBeing.getCode(), userMessage);
						
						log.info("world: {}, entityId: {}, message: {}",
								actingBeing.getCurWorld(), actingBeing.getCode(), userMessage.getMessageKey());
					}
				});
				
				
				break;
				
			default:
				break;
		
		}
		
	}
	
}
