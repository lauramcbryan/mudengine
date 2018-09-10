package com.jpinfo.mudengine.item.notification;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@Aspect
@Component
public class NotificationAspect {
	
	@Autowired
	private ItemRepository repository;

	@PersistenceContext
	private EntityManager em;
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.save(..)) && args(afterItem)")
	public Object compareItems(ProceedingJoinPoint pjp, MudItem afterItem) throws Throwable {
		
		// Object returned by the save operation
		Object savedItem;
		
		if (afterItem.getCode()!=null) {
			
			// This operation is important as the entity at this time will be in managed state,
			// all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudItem from persistenceContext in order to force it to retrieve another.
			em.detach(afterItem);
	
			// Getting the 'before' entity
			Optional<MudItem> optBeforeItem = repository.findById(afterItem.getCode());

			// Perform the save operation as I already have the before and after entities
			savedItem = pjp.proceed();
			
			if (optBeforeItem.isPresent()) {
				
				// Isolating the before entity to help in further comparisons
				MudItem beforeItem= optBeforeItem.get();
			
				// Comparing before and after items
				
				// Looking for itemClass changes
				checkItemClassChanges(beforeItem, afterItem);
				
				// Looking for ownership changes
				checkOwnershipChanges(beforeItem, afterItem);
				
				// Looking for quantity changes
				checkQuantityChanges(beforeItem, afterItem);
				
			}
			
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
		NotificationMessage itemNotification = new NotificationMessage();
		
		itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
		itemNotification.setEntityId(destroyedItem.getCode());
		itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_DESTROY);
		itemNotification.setMessageKey(ItemHelper.ITEM_DESTROY_MSG);
		itemNotification.setArgs(new String[] { 
				destroyedItem.getName()!=null ? destroyedItem.getName() : destroyedItem.getItemClass().getName() 
						}
		);
		
		// TODO: Send Notification
	}

	
	private void checkItemClassChanges(MudItem beforeItem, MudItem afterItem) {
		
		if (!beforeItem.getItemClass().getCode().equals(afterItem.getItemClass().getCode())) {

			NotificationMessage itemNotification = new NotificationMessage();
			
			itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
			itemNotification.setEntityId(afterItem.getCode());
			
			if (afterItem.getCurOwner()!=null) {
				itemNotification.setEntity(NotificationMessage.EnumEntity.BEING);
				itemNotification.setTargetEntityId(afterItem.getCurOwner());
			} else {
				itemNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
				itemNotification.setTargetEntityId(afterItem.getCurPlaceCode().longValue());
				itemNotification.setWorldName(afterItem.getCurWorld());
			}
			
			itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_CLASS_CHANGE);
			itemNotification.setMessageKey(ItemHelper.ITEM_CLASS_CHANGE_MSG);
			itemNotification.setArgs(new String[] { 
					beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
							afterItem.getItemClass().getName()
							}
			);

			// TODO: Send the notification
		}
	}
	
	private void checkOwnershipChanges(MudItem beforeItem, MudItem afterItem) {
		
		if ((beforeItem.getCurOwner()==null) && (afterItem.getCurOwner()!=null)) {
			// send a item.taken event
			
			NotificationMessage itemNotification = new NotificationMessage();
			
			itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
			itemNotification.setEntityId(afterItem.getCode());
			itemNotification.setTargetEntity(NotificationMessage.EnumEntity.BEING);
			itemNotification.setTargetEntityId(afterItem.getCurOwner());
			itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_TAKEN);
			itemNotification.setMessageKey(ItemHelper.ITEM_TAKEN_MSG);
			itemNotification.setArgs(new String[] { 
					beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
							afterItem.getItemClass().getName()
							}
			);

			// TODO: Send Notification
		}
		
		if ((beforeItem.getCurOwner()!=null) && (afterItem.getCurOwner()==null)) {
			// send a item.drop event

			NotificationMessage itemNotification = new NotificationMessage();
			
			itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
			itemNotification.setEntityId(afterItem.getCode());
			itemNotification.setTargetEntity(NotificationMessage.EnumEntity.BEING);
			itemNotification.setTargetEntityId(afterItem.getCurOwner());
			
			itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_DROP);
			itemNotification.setMessageKey(ItemHelper.ITEM_DROP_MSG);
			itemNotification.setArgs(new String[] { 
					beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
							afterItem.getItemClass().getName()
							}
			);
			
			// TODO: Send Notification
			
		}
		
	}
	
	private void checkQuantityChanges(MudItem beforeItem, MudItem afterItem) {
		
		if (afterItem.getCurOwner()!=null) {
		
			if (beforeItem.getQuantity() > afterItem.getQuantity()) {
				
				NotificationMessage itemNotification = new NotificationMessage();
				
				itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
				itemNotification.setEntityId(afterItem.getCode());

				if (afterItem.getCurOwner()!=null) {
					itemNotification.setEntity(NotificationMessage.EnumEntity.BEING);
					itemNotification.setTargetEntityId(afterItem.getCurOwner());
				} else {
					itemNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
					itemNotification.setTargetEntityId(afterItem.getCurPlaceCode().longValue());
					itemNotification.setWorldName(afterItem.getCurWorld());
				}
				
				itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_QTTY_DECREASE);
				itemNotification.setMessageKey(ItemHelper.ITEM_QTTY_DECREASE_MSG);
				itemNotification.setArgs(new String[] { 
						String.valueOf(afterItem.getQuantity()),
						beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
								afterItem.getItemClass().getName()
								}
				);
				
				// TODO: Send Notification
				
			}
			
			if (beforeItem.getQuantity() < afterItem.getQuantity()) {
				
				NotificationMessage itemNotification = new NotificationMessage();
				
				itemNotification.setEntity(NotificationMessage.EnumEntity.ITEM);
				itemNotification.setEntityId(afterItem.getCode());
				
				if (afterItem.getCurOwner()!=null) {
					itemNotification.setEntity(NotificationMessage.EnumEntity.BEING);
					itemNotification.setTargetEntityId(afterItem.getCurOwner());
				} else {
					itemNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
					itemNotification.setTargetEntityId(afterItem.getCurPlaceCode().longValue());
					itemNotification.setWorldName(afterItem.getCurWorld());
				}

				itemNotification.setEvent(NotificationMessage.EnumNotificationEvent.ITEM_QTTY_INCREASE);
				itemNotification.setMessageKey(ItemHelper.ITEM_QTTY_INCREASE_MSG);
				itemNotification.setArgs(new String[] { 
						String.valueOf(afterItem.getQuantity()),
						beforeItem.getName()!=null ? beforeItem.getName() : beforeItem.getItemClass().getName(),
								afterItem.getItemClass().getName()
								}
				);
				
				// TODO: Send Notification
				
			}
		}
		
	}
	
}
