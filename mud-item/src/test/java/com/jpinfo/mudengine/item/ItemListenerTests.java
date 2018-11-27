package com.jpinfo.mudengine.item;

import static org.mockito.BDDMockito.*;
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

import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.item.fixture.ItemNotificationTemplates;
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.notification.NotificationListener;
import com.jpinfo.mudengine.item.repository.ItemRepository;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"item.exchange=" + ItemListenerTests.ITEM_EXCHANGE})
public class ItemListenerTests {
	
	public static final String ITEM_EXCHANGE = "item.exchange";
	
	@MockBean
	private ItemRepository repository;
	
	@Autowired
	private NotificationListener mockListener;
	
	@Autowired
	private TokenService tokenService;

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
		mockListener.receiveNotification(tokenService.buildInternalToken(), msg);
		
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
		mockListener.receiveNotification(tokenService.buildInternalToken(), msg);

		// Check if all items was deleted
		itemsOwned.stream()
			.forEach(d -> {
				
				d.setCurOwner(null);
				d.setCurWorld(msg.getWorldName());
				d.setCurPlaceCode(msg.getTargetEntityId().intValue());
				
				verify(repository).save(d);
			});
		
	}
}
