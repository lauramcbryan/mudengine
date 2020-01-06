package com.jpinfo.mudengine.being.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

@Service
public class NotificationItemService {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationItemService.class);
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private MessageServiceClient messageService;
	
	
	public void handleItemNotification(NotificationMessage notification) {

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
					repository.findPlayableInThisPlace(
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
						
					
						// Gathering all beings in the same place than the acting being
						repository.findPlayableInThisPlace(
										actingBeing.getCurWorld(), 
										actingBeing.getCurPlaceCode())
							.stream()
							.forEach(curBeing -> {
								
								MessageRequest dropMessage = new MessageRequest();
								dropMessage.addChangedEntity(EnumEntityType.ITEM, notification.getEntityId());
								
								if (curBeing.getCode().equals(actingBeing.getCode())) {
									
									// Setting the message to owner
									dropMessage.setMessageKey(BeingHelper.BEING_DROP_YOURS_MSG);
									dropMessage.setArgs(notification.getArgs());
									
								} else {
									
									// Setting the message to others
									dropMessage.setMessageKey(BeingHelper.BEING_DROP_ANOTHER_MSG);
									dropMessage.setArgs(new String[] {
											actingBeing.getName(),
											notification.getArgs()[0]
									});
								}
								
								// Send a message
								messageService.putMessage(curBeing.getCode(), dropMessage);
								
								log.info("world: {}, entityId: {}, message: {}",
										curBeing.getCurWorld(), curBeing.getCode(), dropMessage.getMessageKey());
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
