package com.jpinfo.mudengine.item;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.fixture.ItemNotificationTemplates;
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.service.NotificationService;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemListenerTests {
	
	@MockBean
	private ItemRepository repository;
	
	@MockBean
	private TokenService mockTokenService;
	
	@Autowired
	private NotificationService notificationService;

	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.item.fixture");
	}
	
	@Test
	public void testPlaceDestroyedNotificationReceived() {
		
		// Create the notification event object
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.PLACE_DESTROY_EVENT);
		
		// List of items in the current place
		List<MudItem> itemsInPlace = 
				Fixture.from(MudItem.class).gimme(3, ItemTemplates.RESPONSE_WITH_PLACE);
		
		given(repository.findByCurWorldAndCurPlaceCode(msg.getWorldName(), msg.getEntityId().intValue()))
			.willReturn(itemsInPlace);
		
		// Launch the notification!
		notificationService.handlePlaceNotification(msg);
		
		// Check if all items was deleted
		itemsInPlace.stream()
			.forEach(d ->
			verify(repository).delete(d)
			);
	}
	
	@Test
	public void testBeingDestroyedNotificationReceived() {
		
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.BEING_DESTROY_EVENT);
		
		// List of items with the current being
		List<MudItem> itemsOwned = 
				Fixture.from(MudItem.class).gimme(3, ItemTemplates.RESPONSE_WITH_OWNER);
		
		given(repository.findByCurOwner(msg.getEntityId()))
			.willReturn(itemsOwned);

		// Launch the notification!
		notificationService.handleBeingNotification(msg);

		// Check if all items was deleted
		itemsOwned.stream()
			.forEach(d -> {
				
				d.setCurOwner(null);
				d.setCurWorld(msg.getWorldName());
				d.setCurPlaceCode(msg.getTargetEntityId().intValue());
				
				verify(repository).save(d);
			});
		
	}
	
	@Test
	public void testPlaceChangeNotificationReceived() {
		
		// Create the notification event object
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.PLACE_CLASS_CHANGE_EVENT);
		
		// Launch the notification!
		notificationService.handlePlaceNotification(msg);
		
		verify(repository, never()).save(ArgumentMatchers.any());
	}
	
	@Test
	public void testBeingChangeNotificationReceived() {
		
		// Create the notification event object
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.BEING_CLASS_CHANGE_EVENT);
		
		// Launch the notification!
		notificationService.handleBeingNotification(msg);
		
		verify(repository, never()).save(ArgumentMatchers.any());
	}
}
