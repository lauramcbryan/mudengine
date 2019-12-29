package com.jpinfo.mudengine.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.Destination;

import org.apache.commons.lang.SerializationUtils;
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
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.service.NotificationService;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemNotificationTests {
	
	private static final String  WORLD_NAME = "test";
	private static final Integer PLACE_CODE = (int)System.currentTimeMillis();
	private static final Long    CUR_OWNER = System.currentTimeMillis();
	
	private static final Integer SMALL_QUANTITY_VALUE = 100;
	private static final Integer BIG_QUANTITY_VALUE = 1000;

	@MockBean
	private JmsTemplate jmsTemplate;
	
	@MockBean
	private TokenService mockTokenService;
	
	@Autowired
	private NotificationService service;
	
	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.item.fixture");
	}
	
	
	@Test
	public void testChangeItemClassNotification() throws Throwable {

		MudItemClass newClass = Fixture.from(MudItemClass.class).gimme(ItemTemplates.VALID);
		
		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_OWNER);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item class
		mockAfterItem.setItemClass(newClass);
		
		List<NotificationMessage> notifications = service.handleItemChange(mockBeforeItem, mockAfterItem);
		
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_CLASS_CHANGE)
			.build();

		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());
	}

	@Test
	public void testItemTakenNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_PLACE);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item owner
		mockAfterItem.setCurPlaceCode(null);
		mockAfterItem.setCurWorld(null);
		mockAfterItem.setCurOwner(ItemNotificationTests.CUR_OWNER);

		// That's the notification that we expect to see being triggered
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_TAKEN)
			.build();

		
		List<NotificationMessage> notifications = service.handleItemChange(mockBeforeItem, mockAfterItem);
		
		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());
	}
	

	@Test
	public void testItemDropNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_OWNER);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item owner
		mockAfterItem.setCurPlaceCode(ItemNotificationTests.PLACE_CODE);
		mockAfterItem.setCurWorld(ItemNotificationTests.WORLD_NAME);
		mockAfterItem.setCurOwner(null);
		
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_DROP)
			.build();
		
		List<NotificationMessage> notifications = service.handleItemChange(mockBeforeItem, mockAfterItem);
		
		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());
		
	}
	
	@Test
	public void testItemIncreaseQttyNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item quantity
		mockBeforeItem.setQuantity(ItemNotificationTests.SMALL_QUANTITY_VALUE);
		mockAfterItem.setQuantity(ItemNotificationTests.BIG_QUANTITY_VALUE);
		
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_QTTY_INCREASE)
			.build();
		
		List<NotificationMessage> notifications = service.handleItemChange(mockBeforeItem, mockAfterItem);
		
		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());		
	}

	@Test
	public void testItemDecreaseQttyNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item quantity
		mockBeforeItem.setQuantity(ItemNotificationTests.BIG_QUANTITY_VALUE);
		mockAfterItem.setQuantity(ItemNotificationTests.SMALL_QUANTITY_VALUE);
		
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_QTTY_DECREASE)
			.build();
		
		List<NotificationMessage> notifications = service.handleItemChange(mockBeforeItem, mockAfterItem);
		
		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());
		
	}
	
	@Test
	public void testItemDestroyNotification() throws Throwable {

		MudItem mockDestroyedItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		
		NotificationMessage expectedItemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockDestroyedItem.getCode())
				.event(EnumNotificationEvent.ITEM_DESTROY)
			.build();
		
		List<NotificationMessage> notifications = service.handleItemDestroy(mockDestroyedItem);
		
		assertThat(notifications.contains(expectedItemNotification));
		
		service.dispatchNotifications(notifications);
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(expectedItemNotification), ArgumentMatchers.any());
		
	}

}
