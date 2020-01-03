package com.jpinfo.mudengine.world;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.MudPlaceExitPK;
import com.jpinfo.mudengine.world.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceNotificationTests {

	
	private static final String NEW_EXIT_DIRECTION = "UP";
	private static final Integer NEW_EXIT_TARGET = 2;
	
	@MockBean
	private JmsTemplate jmsTemplate;
	
	@MockBean
	private TokenService tokenUtils;
	
	@Autowired
	private NotificationService service;
	
	@Test
	public void testPlaceDestroy() throws IOException {
		
		List<NotificationMessage> notifications =
				service.handlePlaceDestroy(
						PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID)
						);

		service.dispatchNotifications(notifications);
		
		//  Check if correct notification was sent
		NotificationMessage placeNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.READ_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_DESTROY)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeNotification), 
				ArgumentMatchers.any());
		
	}
	
	@Test
	public void testPlaceClassChanges() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);
		
		afterPlace.setPlaceClass(
				PlaceTestData.loadMudPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS)
				);
		
		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID),
						afterPlace
						);

		service.dispatchNotifications(notifications);
		
		//  Check if correct notification was sent
		NotificationMessage placeClassNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.READ_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_CLASS_CHANGE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeClassNotification), 
				ArgumentMatchers.any());		
	}
	
	@Test
	public void testNewlyCreatedExits() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);

		// Creating the new exit
		MudPlaceExit newExit = new MudPlaceExit();
		newExit.setPk(new MudPlaceExitPK());
		newExit.getPk().setDirection(PlaceNotificationTests.NEW_EXIT_DIRECTION);
		newExit.setTargetPlaceCode(NEW_EXIT_TARGET);
		
		afterPlace.getExits().add(newExit);
		
		
		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID),
						afterPlace
						);
		
		service.dispatchNotifications(notifications);
		
		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.READ_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_CREATE)
			.build();
		
		NotificationMessage placeExit2Notification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(NEW_EXIT_TARGET.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_CREATE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExit2Notification), 
				ArgumentMatchers.any());
	}
	
	@Test
	public void testCloseExit() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);
		afterPlace.getExits().iterator().next().setOpened(false);

		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID),
						afterPlace
						);
		
		service.dispatchNotifications(notifications);

		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.READ_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_CLOSE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());		
		
	}
	
	@Test
	public void testLockExit() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);
		afterPlace.getExits().iterator().next().setLocked(true);

		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID),
						afterPlace
						);
		
		service.dispatchNotifications(notifications);

		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.READ_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_LOCK)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());		
		
	}
	
	
	@Test
	public void testOpenExit() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.TARGET_PLACE_ID);
		afterPlace.getExits().iterator().next().setOpened(true);

		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.TARGET_PLACE_ID),
						afterPlace
						);
		
		service.dispatchNotifications(notifications);

		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.TARGET_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_OPEN)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());		
		
	}
	
	@Test
	public void testUnlockExit() throws IOException {
		
		MudPlace afterPlace = PlaceTestData.loadMudPlace(PlaceTestData.TARGET_PLACE_ID);
		afterPlace.getExits().iterator().next().setLocked(false);

		List<NotificationMessage> notifications =
				service.handlePlaceChange(
						PlaceTestData.loadMudPlace(PlaceTestData.TARGET_PLACE_ID),
						afterPlace
						);
		
		service.dispatchNotifications(notifications);

		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTestData.TARGET_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_UNLOCK)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());		
		
	}

}
