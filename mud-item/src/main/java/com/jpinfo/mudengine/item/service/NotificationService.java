package com.jpinfo.mudengine.item.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@Component
public class NotificationService {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	
	@Autowired
	private ItemRepository repository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Value("${item.topic:disabled}")
	private String itemTopicName;
	
	private ActiveMQTopic itemTopic;

	@PostConstruct
	public void setup() {
		itemTopic = new ActiveMQTopic(itemTopicName);
	}
	
	public List<NotificationMessage> handleItemDestroy(MudItem destroyedItem) {
		
		// Prepare a notification for this change
		NotificationMessage itemNotification = NotificationMessage.builder()
				// Who ?
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(destroyedItem.getCode())
				// What happened?
				.event(NotificationMessage.EnumNotificationEvent.ITEM_DESTROY)
				// Spread the news!
				.messageKey(ItemHelper.ITEM_DESTROY_MSG)
				.args(new String[] { 
					destroyedItem.getName()!=null ? destroyedItem.getName() : destroyedItem.getItemClass().getName() 
					})
				// To this guys (the holder or the place)
				.targetEntity(destroyedItem.getCurOwner()!=null ? EnumEntity.BEING: EnumEntity.PLACE)
				.targetEntityId(destroyedItem.getCurOwner()!=null ? destroyedItem.getCurOwner(): destroyedItem.getCurPlaceCode().longValue())
				.worldName(destroyedItem.getCurWorld())
			.build();
		
		
		return List.of(itemNotification);
	}
	
	public List<NotificationMessage> handleItemChange(MudItem beforeItem, MudItem afterItem) {
		
		List<NotificationMessage> notifications = new ArrayList<>();
		
		// Comparing before and after items
		
		// Looking for itemClass changes
		checkItemClassChanges(beforeItem, afterItem, notifications);
		
		// Looking for ownership changes
		checkOwnershipChanges(beforeItem, afterItem, notifications);
		
		// Looking for quantity changes
		checkIncreaseQuantityChange(beforeItem, afterItem, notifications);
		checkDecreaseQuantityChange(beforeItem, afterItem, notifications);
		
		return notifications;
	}
	
	public void dispatchNotifications(List<NotificationMessage> notifications) {
		
		notifications.stream()
		.forEach(itemNotification -> {
			
			// Send the notification
			jmsTemplate.convertAndSend(itemTopic, itemNotification, m -> {
				
				if (SecurityContextHolder.getContext().getAuthentication()!=null) {
				
					m.setObjectProperty(CommonConstants.AUTH_TOKEN_HEADER, 
							SecurityContextHolder.getContext().getAuthentication().getCredentials());
				}
				
				return m;
			});
			
			log.info("world: {}, entityId: {}, event: {}",
					itemNotification.getWorldName(),
					itemNotification.getEntityId(),
					itemNotification.getEvent()
					);

		});
		
	}
	
	public void handlePlaceNotification(NotificationMessage msg) {

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
	
	public void handleBeingNotification(NotificationMessage msg) {
		
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
	
	
private void checkItemClassChanges(MudItem beforeItem, MudItem afterItem, List<NotificationMessage> notifications) {
		
		if (!beforeItem.getItemClass().getCode().equals(afterItem.getItemClass().getCode())) {

			NotificationMessage itemNotification = NotificationMessage.builder()
					// Who?
					.entity(NotificationMessage.EnumEntity.ITEM)
					.entityId(afterItem.getCode())
					// What happened?
					.event(EnumNotificationEvent.ITEM_CLASS_CHANGE)
					// Spread the news!
					.messageKey(ItemHelper.ITEM_CLASS_CHANGE_MSG)
					.args(new String[] { 
							beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
							afterItem.getItemClass().getName()
							})
					// For this guys (the holder or the place)
					.targetEntity(afterItem.getCurOwner()!=null ? EnumEntity.BEING: EnumEntity.PLACE)
					.targetEntityId(afterItem.getCurOwner()!=null ? afterItem.getCurOwner(): afterItem.getCurPlaceCode().longValue())
					.worldName(afterItem.getCurWorld())
				.build();

			// Enqueue the Notification
			notifications.add(itemNotification);
		}
	}
	
	private void checkOwnershipChanges(MudItem beforeItem, MudItem afterItem, List<NotificationMessage> notifications) {
		
		if ((beforeItem.getCurOwner()==null) && (afterItem.getCurOwner()!=null)) {
			// send a item.taken event
			
			NotificationMessage itemNotification = NotificationMessage.builder()
					// Who?
					.entity(EnumEntity.ITEM)
					.entityId(afterItem.getCode())
					// What happened?
					.event(EnumNotificationEvent.ITEM_TAKEN)
					// Spread the news!
					.messageKey(ItemHelper.ITEM_TAKEN_MSG)
					.args(new String[] { 
							beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName()
							})
					// To this guy! (the taker)
					.targetEntity(EnumEntity.BEING)
					.targetEntityId(afterItem.getCurOwner())
				.build();

			// Enqueue the Notification
			notifications.add(itemNotification);
		}
		
		if ((beforeItem.getCurOwner()!=null) && (afterItem.getCurOwner()==null)) {
			// send a item.drop event

			NotificationMessage itemNotification = NotificationMessage.builder()
					// Who ?
					.entity(EnumEntity.ITEM)
					.entityId(afterItem.getCode())
					// What happened?
					.event(EnumNotificationEvent.ITEM_DROP)
					// Spread the news!
					.messageKey(ItemHelper.ITEM_DROP_MSG)
					.args(new String[] { 
							beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName()
									})
					// To this guy (the former holder)
					.targetEntity(EnumEntity.BEING)
					.targetEntityId(afterItem.getCurOwner())
				.build();
			
			// Enqueue the Notification
			notifications.add(itemNotification);
		}
		
	}
	
	private void checkIncreaseQuantityChange(MudItem beforeItem, MudItem afterItem, List<NotificationMessage> notifications) {
		
		if (beforeItem.getQuantity() < afterItem.getQuantity()) {
			
			NotificationMessage itemNotification = NotificationMessage.builder()
					// Who?
					.entity(EnumEntity.ITEM)
					.entityId(afterItem.getCode())
					// What happened?
					.event(EnumNotificationEvent.ITEM_QTTY_INCREASE)
					// Spread the news!
					.messageKey(ItemHelper.ITEM_QTTY_INCREASE_MSG)
					.args(new String[] { 
									String.valueOf(afterItem.getQuantity()),
									beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName()
								})
					// To this guys (the holder or the place)
					.targetEntity(afterItem.getCurOwner()!=null ? EnumEntity.BEING: EnumEntity.PLACE)
					.targetEntityId(afterItem.getCurOwner()!=null ? afterItem.getCurOwner(): afterItem.getCurPlaceCode().longValue())
					.worldName(afterItem.getCurWorld())
				.build();
			
			// Enqueue the Notification
			notifications.add(itemNotification);
			
		}
	}

	
	
	private void checkDecreaseQuantityChange(MudItem beforeItem, MudItem afterItem, List<NotificationMessage> notifications) {
		
		if (beforeItem.getQuantity() > afterItem.getQuantity()) {
			
			NotificationMessage itemNotification = NotificationMessage.builder()
					// Who?
					.entity(EnumEntity.ITEM)
					.entityId(afterItem.getCode())
					// What happened?
					.event(EnumNotificationEvent.ITEM_QTTY_DECREASE)
					// Spread the news!
					.messageKey(ItemHelper.ITEM_QTTY_DECREASE_MSG)
					.args(new String[] { 
									String.valueOf(afterItem.getQuantity()),
									beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName()
								})
					// To this guys (the holder or the place)
					.targetEntity(afterItem.getCurOwner()!=null ? EnumEntity.BEING: EnumEntity.PLACE)
					.targetEntityId(afterItem.getCurOwner()!=null ? afterItem.getCurOwner(): afterItem.getCurPlaceCode().longValue())
					.worldName(afterItem.getCurWorld())
				.build();
			
			// Enqueue the Notification
			notifications.add(itemNotification);
			
		}
	}

}
