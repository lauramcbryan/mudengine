package com.jpinfo.mudengine.being.notification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.service.NotificationBeingService;
import com.jpinfo.mudengine.common.message.MessageRequest;

@Aspect
@Component
public class NotificationAspect {
	
	@Autowired
	private NotificationBeingService beingService;

	@Autowired
	private BeingRepository repository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.save(..)) && args(afterBeing)")
	public Object compareBeing(ProceedingJoinPoint pjp, MudBeing afterBeing) throws Throwable {
		
		// Object returned after save operation
		Object savedBeing;
		
		// Checking if the future state entity has a ID as this method
		// is also used to create entities
		// Additionally, we only throw notification for playable beings.
		if ((afterBeing.getCode()!=null) && (afterBeing.getPlayerId()!=null)) {

			// This operation is important as the entity at this time will be in managed state,
			// all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudBeing from persistenceContext in order to force it to retrieve another.
			em.detach(afterBeing);
	
			List<MessageRequest> msgList =
					// Getting the 'before' entity					
					repository.findById(afterBeing.getCode())
					// Comparing both beings
					.map(beforeBeing -> beingService.handleBeingChange(beforeBeing, afterBeing))
					.orElse(new ArrayList<>());
			
			// Execute the save operation
			savedBeing = pjp.proceed();
			
			// Pass through the notification list
			beingService.dispatchMessages(msgList, afterBeing);

		} else {
			// In this case a place is being created, just proceed
			// (or it's a non playable being)
			savedBeing = pjp.proceed();
		}
		
		return savedBeing;
	}
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.delete(..)) && args(destroyedBeing)")
	public void sendDestroyNotification(ProceedingJoinPoint pjp, MudBeing destroyedBeing) throws Throwable {

		// Perform the delete operation
		pjp.proceed();
		
		beingService.handleDestroyedBeing(destroyedBeing);

	}
}
