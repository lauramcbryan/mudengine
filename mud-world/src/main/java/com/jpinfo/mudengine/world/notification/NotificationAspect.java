package com.jpinfo.mudengine.world.notification;

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
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.service.NotificationService;

@Aspect
@Component
public class NotificationAspect {

	@Autowired
	private NotificationService service;
	
	@Autowired
	private PlaceRepository repository;
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * This join point intercepts all place saves performed by the service. 
	 * 
	 * For each save operation, we compare the current status of the object in database against the future state object.
	 * 
	 * @param pjp - object that holds the actual call.  Only after this call succeeded we send the notifications
	 * @param afterPlace - future state of the place being altered
	 */
	@Around(value = "execution(public * com.jpinfo.mudengine.world.repository.PlaceRepository+.save(..)) && args(afterPlace)")
	public Object comparePlaces(ProceedingJoinPoint pjp, MudPlace afterPlace) throws Throwable {

		// Object returned after save operation
		Object savedPlace;
		
		// Checking if the future state entity has a PK as this method
		// is also used to create entities
		if (afterPlace.getCode()!=null) {
			
			// This operation is important as the entity at this time will be in managed state,
			// therefore all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudPlace from persistenceContext in order to force it to retrieve another.
			em.detach(afterPlace);
			
			List<NotificationMessage> notifications =
					// Getting the 'before' entity					
					repository.findById(afterPlace.getCode())
					// Comparing before and after items					
					.map(beforePlace -> service.handlePlaceChange(beforePlace, afterPlace))
					.orElse(new ArrayList<NotificationMessage>());
			
			// Execute the save operation
			savedPlace = pjp.proceed();
			
			// Dispatch the notifications collected before
			// (Only after the previous database operation succeed)
			service.dispatchNotifications(notifications);			

		} else {
			// In this case a place is being created, just proceed
			// (this use case will generate further notifications when the place will be
			// updated with the new exit)
			savedPlace = pjp.proceed();
		}
		
		return savedPlace;
	}
	
	/**
	 * This join point intercepts all place destructions.
	 * 
	 * When this happens, it's expected that the place destruction event 
	 * be carried over to the being service (destroying all beings in the place)
	 * and the item service (destroying them as well).
	 * In previous versions, that was accomplished through a direct call from Mud-World
	 * to Mud-Being and Mud-Item projects.  However, that implied direct coupling and
	 * increase in original transaction.
	 * As these cascading secondary events (being destruction, item destruction) aren't critical
	 * (in fact, from Place service perspective, it's more like a fire-and-forget call)
	 * they will be accomplished through asynchronous notification triggered at this point.
	 * 
	 * @param pjp - object that holds the actual destroy operation.  Called during the process.
	 * @param destroyedPlace - the place being destroyed
	 * @throws Throwable
	 */
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.delete(..)) && args(destroyedPlace)")
	public void sendDestroyNotification(ProceedingJoinPoint pjp, MudPlace destroyedPlace) throws Throwable {

		// First of all, execute the change in database
		pjp.proceed();
		
		// Build (and dispatch) notifications
		service.dispatchNotifications(
				service.handlePlaceDestroy(destroyedPlace)
				);
	}
}
