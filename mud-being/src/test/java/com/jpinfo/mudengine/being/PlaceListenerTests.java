package com.jpinfo.mudengine.being;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.being.client.ItemServiceClient;
import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.fixture.PlaceNotificationTemplates;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.notification.NotificationListener;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
		"being.topic=" + BeingTests.BEING_EXCHANGE,
		"place.topic=" + BeingTests.PLACE_EXCHANGE,
		"item.topic=" + BeingTests.ITEM_EXCHANGE
		})
public class PlaceListenerTests {
	
	// This mock bean isn't used during validation.
	// It's mocked just to avoid having it trying to call outside world
	@MockBean
	private ItemServiceClient mockItem;

	@MockBean
	private BeingRepository repository;
	
	@MockBean
	private MessageServiceClient messageService;
	
	@Autowired
	private NotificationListener mockListener;
	
	@Autowired
	private TokenService tokenService;

	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
	}
	
	@Test
	public void testPlaceDestroyedNotificationReceived() {
		
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(PlaceNotificationTemplates.PLACE_DESTROY);

		// Beings in the same place
		List<MudBeing> otherMudBeings = 
				Fixture.from(MudBeing.class).gimme(3, 
						BeingTemplates.SIMPLE, 
						BeingTemplates.PLAYABLE, 
						BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct being repository to return other beings in the place when requested
		given(repository.findByCurWorldAndCurPlaceCode(msg.getWorldName(), msg.getEntityId().intValue()))
			.willReturn(otherMudBeings);
		
		// Launch the notification!
		mockListener.receiveNotification(tokenService.buildInternalToken(), msg);
		
		// Check if all beings are being destroyed
		otherMudBeings.stream()
			.forEach(d -> 
				verify(repository).delete(d)
				);
		
	}
	
	@Test
	public void testPlaceExitCreatedNotificationReceived() {
		
		testNotificationReceived(PlaceNotificationTemplates.PLACE_EXIT_CREATE);
	}

	@Test
	public void testPlaceExitOpenedNotificationReceived() {
		
		testNotificationReceived(PlaceNotificationTemplates.PLACE_EXIT_OPEN);
	}

	@Test
	public void testPlaceExitClosedNotificationReceived() {
		
		testNotificationReceived(PlaceNotificationTemplates.PLACE_EXIT_CLOSE);
	}

	@Test
	public void testPlaceExitUnlockedNotificationReceived() {
		
		testNotificationReceived(PlaceNotificationTemplates.PLACE_EXIT_UNLOCK);
	}

	@Test
	public void testPlaceExitLockedNotificationReceived() {
		
		testNotificationReceived(PlaceNotificationTemplates.PLACE_EXIT_LOCK);
	}

	@Test
	public void testPlaceClassChangedNotificationReceived() {
		testNotificationReceived(PlaceNotificationTemplates.PLACE_CLASS_CHANGE);
	}

	
	
	
	private void testNotificationReceived(String label) {
		
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(label);

		// Beings in the same place
		List<MudBeing> otherMudBeings = 
				Fixture.from(MudBeing.class).gimme(3, 
						BeingTemplates.SIMPLE, 
						BeingTemplates.PLAYABLE, 
						BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct being repository to return other beings in the place when requested
		given(repository.findByCurWorldAndCurPlaceCode(msg.getWorldName(), msg.getEntityId().intValue()))
			.willReturn(otherMudBeings);
		
		// Launch the notification!
		mockListener.receiveNotification(tokenService.buildInternalToken(), msg);
		
		// Check if proper message was sent to other beings
		otherMudBeings.stream()
			.filter(d -> d.getPlayerId()!=null)
			.forEach(d -> {
				
				// Preparing the message request to compare against
				MessageRequest msgRequest = new MessageRequest();
				msgRequest.setMessageKey(msg.getMessageKey());
				msgRequest.setArgs(msg.getArgs());
				msgRequest.addChangedEntity(EnumEntityType.PLACE, msg.getEntityId());
				
				verify(messageService).putMessage(d.getCode(), msgRequest);
			});
		
	}
	
	
}
