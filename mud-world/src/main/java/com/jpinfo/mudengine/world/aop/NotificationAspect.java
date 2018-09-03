package com.jpinfo.mudengine.world.aop;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@Aspect
@Component
public class NotificationAspect {

	@Autowired
	private PlaceRepository repository;
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.save(..)) && args(afterPlace)")
	public Object comparePlaces(ProceedingJoinPoint pjp, MudPlace afterPlace) throws Throwable {
		
		Object savedPlace;
		
		if (afterPlace.getCode()!=null) {

			List<NotificationMessage> messages = new ArrayList<>();			
			
			Optional<MudPlace> optBeforePlace = repository.findById(afterPlace.getCode());
			
			if (optBeforePlace.isPresent()) {
				
				// Retrieving the old object
				MudPlace beforePlace = optBeforePlace.get();
			
				// Comparing before and after places
				
				// Looking for placeClass changes
				checkPlaceClassChanges(beforePlace, afterPlace, messages);
				
				
				// Looking for newly-created exits
				checkNewlyCreatedExits(beforePlace, afterPlace, messages);
				
				// Looking for updated exits
				checkUpdatedExits(beforePlace, afterPlace, messages);

			}
				
			// Execute the operation
			savedPlace = pjp.proceed();
			
			// TODO: Send the notifications
			messages.stream().forEach(msg -> {
				
				System.out.println(msg);
				
			});

		} else {
			// Creating a place; proceed
			savedPlace = pjp.proceed();
		}
		
		return savedPlace;
	}
	
	@Around(value = "execution(public * org.springframework.data.repository.Repository+.delete(..)) && args(destroyedPlace)")
	public void sendDestroyNotification(ProceedingJoinPoint pjp, MudPlace destroyedPlace) throws Throwable {
		
		// Prepare a notification for this change
		NotificationMessage placeNotification = new NotificationMessage();
		
		placeNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
		placeNotification.setEntityId(destroyedPlace.getCode().longValue());
		placeNotification.setEvent(NotificationMessage.EnumNotificationEvent.PLACE_DESTROY);
		placeNotification.setMessageKey(WorldHelper.PLACE_DESTROY_MSG);
		placeNotification.setArgs(new String[] { 
				destroyedPlace.getName()!=null ? destroyedPlace.getName() : destroyedPlace.getPlaceClass().getName() 
						}
		);
		
		pjp.proceed();
		
		// TODO: Send the notification
		
	}
	
	private void checkPlaceClassChanges(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> messages) {
		
		if (!beforePlace.getPlaceClass().getCode().equals(afterPlace.getPlaceClass().getCode())) {
			
			NotificationMessage placeNotification = new NotificationMessage();
			
			placeNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
			placeNotification.setEntityId(afterPlace.getCode().longValue());
			placeNotification.setEvent(NotificationMessage.EnumNotificationEvent.PLACE_CLASS_CHANGE);
			placeNotification.setMessageKey(WorldHelper.PLACE_DESTROY_MSG);
			placeNotification.setArgs(new String[] { 
					beforePlace.getName()!=null ? beforePlace.getName() : beforePlace.getPlaceClass().getName(),
							afterPlace.getPlaceClass().getName()
							}
			);
			
			messages.add(placeNotification);
			
		}
	}
	
	private void checkNewlyCreatedExits(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> messages) {
		
		afterPlace.getExits().stream()
			.filter(d -> !beforePlace.getExits().contains(d))
			.forEach(d -> {
				
				// Create exit notification
				NotificationMessage placeNotification = new NotificationMessage();
				
				placeNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
				placeNotification.setEntityId(afterPlace.getCode().longValue());
				placeNotification.setEvent(NotificationMessage.EnumNotificationEvent.PLACE_EXIT_CREATE);
				placeNotification.setMessageKey(WorldHelper.PLACE_EXIT_CREATE_MSG);
				placeNotification.setArgs(new String[] { d.getPk().getDirection() }
				);
				
				messages.add(placeNotification);
				
			});
		
	}
	
	private void checkUpdatedExits(MudPlace beforePlace, MudPlace afterPlace, List<NotificationMessage> messages) {

		// Looking for exit changes
		beforePlace.getExits().stream()
			.filter(d -> afterPlace.getExits().contains(d))
			.forEach(beforeExit -> 
				
				// Search the afterExit
				afterPlace.getExits().stream()
					.filter(afterExit -> afterExit.equals(beforeExit))
					.findFirst()
					.ifPresent(afterExit -> 
						checkOneUpdatedExit(afterPlace.getCode(), beforeExit, afterExit, messages)
					)
			);
	}
	
	
	private void checkOneUpdatedExit(Integer placeCode, MudPlaceExit beforeExit, MudPlaceExit afterExit, List<NotificationMessage> messages) {
		
		if (beforeExit.isOpened() && !afterExit.isOpened()) {
			// send place.exit.close notification
			
			messages.add(
				buildExitChangeNotification(placeCode, beforeExit.getDirection(), 
						NotificationMessage.EnumNotificationEvent.PLACE_EXIT_CLOSE, 
						WorldHelper.PLACE_EXIT_CLOSE_MSG)
			);
		}
		
		if (!beforeExit.isOpened() && afterExit.isOpened()) {
			// send place.exit.open notification

			messages.add(
				buildExitChangeNotification(placeCode, beforeExit.getDirection(), 
						NotificationMessage.EnumNotificationEvent.PLACE_EXIT_OPEN, 
						WorldHelper.PLACE_EXIT_OPEN_MSG)
			);
			
		}
		
		if (beforeExit.isLocked() && !afterExit.isLocked()) {
			// send place.exit.unlock notification

			messages.add(
				buildExitChangeNotification(placeCode, beforeExit.getDirection(), 
						NotificationMessage.EnumNotificationEvent.PLACE_EXIT_UNLOCK, 
						WorldHelper.PLACE_EXIT_UNLOCK_MSG)
			);
		}
		
		if (!beforeExit.isLocked() && afterExit.isLocked()) {
			// send place.exit.lock notification
			
			buildExitChangeNotification(placeCode, beforeExit.getDirection(), 
					NotificationMessage.EnumNotificationEvent.PLACE_EXIT_LOCK, 
					WorldHelper.PLACE_EXIT_LOCK_MSG);
			
		}
	}

	
	private NotificationMessage buildExitChangeNotification(Integer placeId, String direction, NotificationMessage.EnumNotificationEvent event, String messageKey) {
		
		NotificationMessage placeNotification = new NotificationMessage();
		
		placeNotification.setEntity(NotificationMessage.EnumEntity.PLACE);
		placeNotification.setEntityId(placeId.longValue());
		placeNotification.setEvent(event);
		placeNotification.setMessageKey(messageKey);
		placeNotification.setArgs(new String[] {direction} );

		return placeNotification;
	}

}
