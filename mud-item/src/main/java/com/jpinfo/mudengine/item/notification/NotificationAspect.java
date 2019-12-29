package com.jpinfo.mudengine.item.notification;

import java.util.ArrayList;
import java.util.List;

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
import com.jpinfo.mudengine.item.service.NotificationService;

@Aspect
@Component
public class NotificationAspect {
	
	@Autowired
	private NotificationService service;
	
	@Autowired
	private ItemRepository repository;

	@PersistenceContext
	private EntityManager em;

	@Around(value = "execution(public * com.jpinfo.mudengine.item.repository.ItemRepository+.save(..)) && args(afterItem)")
	public Object compareItems(ProceedingJoinPoint pjp, MudItem afterItem) throws Throwable {
		
		// Object returned by the save operation
		Object savedItem;
		
		if (afterItem.getCode()!=null) {
			
			// This operation is important as the entity at this time will be in managed state,
			// all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudItem from persistenceContext in order to force it to retrieve another.
			em.detach(afterItem);
	
			List<NotificationMessage> notifications =
					// Getting the 'before' entity					
					repository.findById(afterItem.getCode())
					// Comparing before and after items					
					.map(beforeItem -> service.handleItemChange(beforeItem, afterItem))
					.orElse(new ArrayList<NotificationMessage>());
			
			// Perform the save operation
			savedItem = pjp.proceed();

			// Dispatch the notifications collected before
			// (Only after the previous database operation succeed)
			service.dispatchNotifications(notifications);
			
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

		// Build (and dispatch) notifications
		service.dispatchNotifications(
				service.handleItemDestroy(destroyedItem)
				);
	}

	
	
}
