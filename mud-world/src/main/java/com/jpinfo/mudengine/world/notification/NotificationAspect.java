package com.jpinfo.mudengine.world.notification;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.player.Session;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumEntity;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@Aspect
@Component
public class NotificationAspect {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationAspect.class);
	
	@Autowired
	private RabbitTemplate rabbit;
	
	@Value("${place.exchange}")
	private String placeExchange;

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
			
			List<NotificationMessage> notifications = new ArrayList<>();

			// This operation is important as the entity at this time will be in managed state,
			// therefore all find calls to database will return the same managed object.
			// To avoid this and get a fresh database version of the entity, we detached the future-state
			// MudPlace from persistenceContext in order to force it to retrieve another.
			em.detach(afterPlace);
	
			// Getting the 'before' entity
			Optional<MudPlace> optBeforePlace = repository.findById(afterPlace.getCode());
			
			if (optBeforePlace.isPresent()) {
				
				// Isolating the before entity to help in further comparisons
				MudPlace beforePlace = optBeforePlace.get();
			
				// Comparing before and after places
				
				// Looking for placeClass changes
				checkPlaceClassChanges(beforePlace, afterPlace, notifications);
				
				// Looking for newly-created exits
				checkNewlyCreatedExits(beforePlace, afterPlace, notifications);
				
				// Looking for updated exits
				checkUpdatedExits(beforePlace, afterPlace, notifications);
			}
			
			// Execute the save operation
			savedPlace = pjp.proceed();
			
			// Pass through the notification list and send the messages
			notifications.stream()
				.forEach(placeNotification -> {
					// Send the notification
					rabbit.convertAndSend(placeExchange, "", placeNotification, m -> {
						m.getMessageProperties().getHeaders()
							.put(CommonConstants.AUTH_TOKEN_HEADER, 
									SecurityContextHolder.getContext().getAuthentication().getCredentials());
						return m;
					});
					
					log.info("world: {}, entityId: {}, event: {}",
							placeNotification.getWorldName(),
							placeNotification.getEntityId(),
							placeNotification.getEvent()
							);
				});
			

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
		
		// Prepare a notification for this change
		NotificationMessage placeNotification = NotificationMessage.builder()
				// Who?
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(destroyedPlace.getCode().longValue())
				// What happened?
				.event(EnumNotificationEvent.PLACE_DESTROY)
				// Spread the news!
				.messageKey(WorldHelper.PLACE_DESTROY_MSG)
				.args(new String[] {
						destroyedPlace.getName()!=null ? destroyedPlace.getName() : destroyedPlace.getPlaceClass().getName()
						})
				// The guys in the place will take interest on that  :D
				.targetEntity(EnumEntity.PLACE)
				.targetEntityId(destroyedPlace.getCode().longValue())
				.worldName(getWorldName())
			.build();
		
		// Send the notification
		rabbit.convertAndSend(placeExchange, "", placeNotification, m -> {
			m.getMessageProperties().getHeaders()
			.put(CommonConstants.AUTH_TOKEN_HEADER, 
					SecurityContextHolder.getContext().getAuthentication().getCredentials());
			return m;
		});

		log.info("world: {}, entityId: {}, event: {}",
				placeNotification.getWorldName(),
				placeNotification.getEntityId(),
				placeNotification.getEvent()
				);
	}

	/**
	 * Compare place class changes and build the corresponding notification object.
	 * 
	 * @param beforePlace - current state of the MudPlace object in database
	 * @param afterPlace - future state of the MudPlace object
	 */
	private void checkPlaceClassChanges(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> notifications) {
		
		if (!beforePlace.getPlaceClass().getCode().equals(afterPlace.getPlaceClass().getCode())) {

			// Prepare a notification for this change
			NotificationMessage placeNotification = NotificationMessage.builder()
					// Who?
					.entity(NotificationMessage.EnumEntity.PLACE)
					.entityId(afterPlace.getCode().longValue())
					// What happened?
					.event(EnumNotificationEvent.PLACE_CLASS_CHANGE)
					// Spread the news!
					.messageKey(WorldHelper.PLACE_CLASS_CHANGE_MSG)
					.args(new String[] {
							beforePlace.getName()!=null ? beforePlace.getName() : beforePlace.getPlaceClass().getName(),
							afterPlace.getPlaceClass().getName()
							})
					// The guys in the place will take interest on that
					.targetEntity(EnumEntity.PLACE)
					.targetEntityId(afterPlace.getCode().longValue())
					.worldName(getWorldName())
				.build();
			
			// Enqueue the notification
			notifications.add(placeNotification);
		}
	}
	
	/**
	 * Look at any newly created exits and build the corresponding notification object.
	 * 
	 * @param beforePlace - current state of the MudPlace object in database
	 * @param afterPlace - future state of the MudPlace object
	 */
	private void checkNewlyCreatedExits(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> notifications) {
		
		afterPlace.getExits().stream()
			.filter(d -> !beforePlace.getExits().contains(d))
			.forEach(d -> {
				
				// Create exit notification
				NotificationMessage placeNotification = NotificationMessage.builder()
						// Who?
						.entity(NotificationMessage.EnumEntity.PLACE)
						.entityId(afterPlace.getCode().longValue())
						// What happened?
						.event(EnumNotificationEvent.PLACE_EXIT_CREATE)
						// Spread the news!
						.messageKey(WorldHelper.PLACE_EXIT_CREATE_MSG)
						.args(new String[] {
								d.getPk().getDirection()
								})
						// The guys in the place will take interest on that
						.targetEntity(EnumEntity.PLACE)
						.targetEntityId(afterPlace.getCode().longValue())
						.worldName(getWorldName())
					.build();

				// Enqueue the notification
				notifications.add(placeNotification);
				
			});
		
	}
	
	/**
	 * Traverse all exits found both in current and future state Place object.
	 * Check changes in each one of them (through checkOneUpdatedExit method).
	 * 
	 * @param beforePlace - current state of the MudPlace object in database
	 * @param afterPlace - future state of the MudPlace object
	 */
	private void checkUpdatedExits(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> notifications) {

		// Looking for exit changes
		beforePlace.getExits().stream()
			.filter(d -> afterPlace.getExits().contains(d))
			.forEach(beforeExit -> 
				
				// Search the afterExit
				afterPlace.getExits().stream()
					.filter(afterExit -> afterExit.equals(beforeExit))
					.findFirst()
					.ifPresent(afterExit -> 
						checkOneUpdatedExit(afterPlace.getCode(), beforeExit, afterExit, notifications)
					)
			);
	}
	

	/**
	 * Check one specific exit for changes and build the corresponding notification object.
	 * 
	 * Fields which change trigger a notification:
	 * - opened
	 * - locked
	 *
	 * @param placeCode - code of the place (used in notifications)
	 * @param beforeExit - current state of the exit.
	 * @param afterExit - future state of the exit.
	 */
	private void checkOneUpdatedExit(Integer placeCode, MudPlaceExit beforeExit, MudPlaceExit afterExit, List<NotificationMessage> notifications) {
		
		if (beforeExit.isOpened() && !afterExit.isOpened()) {

			// send place.exit.close notification
			sendExitChangeNotification(placeCode, beforeExit.getDirection(), 
					NotificationMessage.EnumNotificationEvent.PLACE_EXIT_CLOSE, 
					WorldHelper.PLACE_EXIT_CLOSE_MSG, notifications);
		}
		
		if (!beforeExit.isOpened() && afterExit.isOpened()) {
			
			// send place.exit.open notification
			sendExitChangeNotification(placeCode, beforeExit.getDirection(), 
					NotificationMessage.EnumNotificationEvent.PLACE_EXIT_OPEN, 
					WorldHelper.PLACE_EXIT_OPEN_MSG, notifications);
		}
		
		if (beforeExit.isLocked() && !afterExit.isLocked()) {

			// send place.exit.unlock notification
			sendExitChangeNotification(placeCode, beforeExit.getDirection(), 
					NotificationMessage.EnumNotificationEvent.PLACE_EXIT_UNLOCK, 
					WorldHelper.PLACE_EXIT_UNLOCK_MSG, notifications);
		}
		
		if (!beforeExit.isLocked() && afterExit.isLocked()) {

			// send place.exit.lock notification
			sendExitChangeNotification(placeCode, beforeExit.getDirection(), 
					NotificationMessage.EnumNotificationEvent.PLACE_EXIT_LOCK, 
					WorldHelper.PLACE_EXIT_LOCK_MSG, notifications);
			
		}
	}


	/**
	 * Helper method to send a notification for any exit change.
	 * 
	 * @param placeId - place where the event happens
	 * @param direction - direction of the affected exit
	 * @param event - code of the event as defined in NotificationMessage.EnumNotificationEvent enumeration
	 * @param messageKey - message to be presented for this event as defined in WorldHelper
	 * @return
	 */
	private void sendExitChangeNotification(Integer placeId, String direction, NotificationMessage.EnumNotificationEvent event, String messageKey, List<NotificationMessage> notifications) {

		NotificationMessage placeNotification = NotificationMessage.builder()
				// Who?
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(placeId.longValue())
				// What happened?
				.event(event)
				// Spread the news!
				.messageKey(messageKey)
				.args(new String[] {direction})
				// The guys in the place will take interest on that
				.worldName(getWorldName())
			.build();
		
		// Enqueue the notification
		notifications.add(placeNotification);
	}
	
	private String getWorldName() {
		
		MudUserDetails uDetails = (MudUserDetails)
				SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		Optional<Session> optSession = uDetails.getSessionData(); 
		
		if (optSession.isPresent())
			return optSession.get().getCurWorldName();
		else
			return null;
	}

}
