package com.jpinfo.mudengine.item;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SerializationUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.item.fixture.ItemTemplates;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.notification.NotificationAspect;
import com.jpinfo.mudengine.item.repository.ItemRepository;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"item.exchange=" + ItemNotificationTests.ITEM_EXCHANGE})
public class ItemNotificationTests {
	
	public static final String ITEM_EXCHANGE = "item.exchange";
	
	private static final String  WORLD_NAME = "test";
	private static final Integer PLACE_CODE = (int)System.currentTimeMillis();
	private static final Long    CUR_OWNER = System.currentTimeMillis();
	
	private static final Integer SMALL_QUANTITY_VALUE = 100;
	private static final Integer BIG_QUANTITY_VALUE = 1000;

	@MockBean
	private RabbitTemplate rabbit;
	
	@MockBean
	private ItemRepository repository;
	
	@Autowired
	private NotificationAspect aspect;
	
	@MockBean
	private ProceedingJoinPoint pjp;
	

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
		
		given(repository.findById(mockBeforeItem.getCode())).willReturn(Optional.of(mockBeforeItem));
		
		
		aspect.compareItems(pjp, mockAfterItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_CLASS_CHANGE)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}

	@Test
	public void testItemTakenNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_PLACE);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item owner
		mockAfterItem.setCurPlaceCode(null);
		mockAfterItem.setCurWorld(null);
		mockAfterItem.setCurOwner(ItemNotificationTests.CUR_OWNER);
		
		given(repository.findById(mockBeforeItem.getCode())).willReturn(Optional.of(mockBeforeItem));
		
		
		aspect.compareItems(pjp, mockAfterItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_TAKEN)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}
	

	@Test
	public void testItemDropNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_WITH_OWNER);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item owner
		mockAfterItem.setCurPlaceCode(ItemNotificationTests.PLACE_CODE);
		mockAfterItem.setCurWorld(ItemNotificationTests.WORLD_NAME);
		mockAfterItem.setCurOwner(null);
		
		given(repository.findById(mockBeforeItem.getCode())).willReturn(Optional.of(mockBeforeItem));
		
		
		aspect.compareItems(pjp, mockAfterItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_DROP)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}
	
	@Test
	public void testItemIncreaseQttyNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item quantity
		mockBeforeItem.setQuantity(ItemNotificationTests.SMALL_QUANTITY_VALUE);
		mockAfterItem.setQuantity(ItemNotificationTests.BIG_QUANTITY_VALUE);
		
		given(repository.findById(mockBeforeItem.getCode())).willReturn(Optional.of(mockBeforeItem));
		
		
		aspect.compareItems(pjp, mockAfterItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_QTTY_INCREASE)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}

	@Test
	public void testItemDecreaseQttyNotification() throws Throwable {

		MudItem mockBeforeItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		MudItem mockAfterItem = (MudItem) SerializationUtils.clone(mockBeforeItem);
		
		// Changing the item quantity
		mockBeforeItem.setQuantity(ItemNotificationTests.BIG_QUANTITY_VALUE);
		mockAfterItem.setQuantity(ItemNotificationTests.SMALL_QUANTITY_VALUE);
		
		given(repository.findById(mockBeforeItem.getCode())).willReturn(Optional.of(mockBeforeItem));
		
		
		aspect.compareItems(pjp, mockAfterItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockBeforeItem.getCode())
				.event(EnumNotificationEvent.ITEM_QTTY_DECREASE)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}
	
	@Test
	public void testItemDestroyNotification() throws Throwable {

		MudItem mockDestroyedItem = Fixture.from(MudItem.class).gimme(ItemTemplates.RESPONSE_FULL);
		
		aspect.sendDestroyNotification(pjp, mockDestroyedItem);
		
		NotificationMessage itemNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.ITEM)
				.entityId(mockDestroyedItem.getCode())
				.event(EnumNotificationEvent.ITEM_DESTROY)
			.build();
		
		verify(rabbit).convertAndSend(ITEM_EXCHANGE, "", itemNotification);
	}
}
