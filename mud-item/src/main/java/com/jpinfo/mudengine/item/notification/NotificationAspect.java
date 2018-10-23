package com.jpinfo.mudengine.item.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@Aspect
@Component
public class NotificationAspect {
	
	@Autowired
	private RabbitTemplate rabbit;
	
	@Value("${item.exchange}")
	private String itemExchange;
	
	@Autowired
	private ItemRepository repository;

	@PersistenceContext
	private EntityManager em;
	
	@Around(value = "execution(public * com.jpinfo.mudengine.item.repository.ItemRepository+.save(..)) && args(afterItem)")
	public Object compareItems(ProceedingJoinPoint pjp, MudItem afterItem) throws Throwable {
		
		// Object returned by the save operation
		Object savedItem;
		
		if (afterItem.getCode()!=null) {
			
			List<NotificationMessage> notifications = new ArrayList<>();
			
			// This operation is important as the entity at this time will be in managed state,
			// all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudItem from persistenceContext in order to force it to retrieve another.
			em.detach(afterItem);
	
			// Getting the 'before' entity
			Optional<MudItem> optBeforeItem = repository.findById(afterItem.getCode());

			
			if (optBeforeItem.isPresent()) {
				
				// Isolating the before entity to help in further comparisons
				MudItem beforeItem= optBeforeItem.get();
			
				// Comparing before and after items
				
				// Looking for itemClass changes
				checkItemClassChanges(beforeItem, afterItem, notifications);
				
				// Looking for ownership changes
				checkOwnershipChanges(beforeItem, afterItem, notifications);
				
				// Looking for quantity changes
				checkIncreaseQuantityChange(beforeItem, afterItem, notifications);
				checkDecreaseQuantityChange(beforeItem, afterItem, notifications);
			}
			
			// Perform the save operation
			savedItem = pjp.proceed();
			
			notifications.stream()
				.forEach(itemNotification -> 
					// Send the notification
					rabbit.convertAndSend(itemExchange, "", itemNotification)
				);
			
			
		} else {
			// Just creating the item; proceed
			savedItem = pjp.proceed();
		}
		
		return savedItem;
	}
	
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.delete(..)) && args(destroyedItem)")
	public void sendDestroyNotification(ProceedingJoinPoint pjp, MudItem destroyedItem) throws Throwable {

		// Perform the delete operation
		pjp.proceed();

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
		
		// Send Notification
		rabbit.convertAndSend(itemExchange, "", itemNotification);
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
