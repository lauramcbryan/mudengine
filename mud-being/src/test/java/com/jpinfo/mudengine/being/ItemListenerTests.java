package com.jpinfo.mudengine.being;

import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SerializationUtils;
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
import com.jpinfo.mudengine.being.fixture.ItemNotificationTemplates;
import com.jpinfo.mudengine.being.fixture.MudBeingProcessor;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.notification.NotificationListener;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.utils.NotificationMessage;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"being.exchange=" + ItemListenerTests.BEING_EXCHANGE})
public class ItemListenerTests {
	
	public static final String BEING_EXCHANGE = "being.exchange";
	
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

	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
	}
	
	@Test
	public void testItemDroppedNotificationReceived() {

		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.ITEM_DROP);
		
		// Being owning the item
		MudBeing owningMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		// Adjusting the being code to reflect the item.owner
		owningMudBeing.setCode(msg.getTargetEntityId());
		
		// Beings in the same place
		List<MudBeing> otherMudBeings = 
				Fixture.from(MudBeing.class).gimme(3, 
						BeingTemplates.SIMPLE, 
						BeingTemplates.PLAYABLE, 
						BeingTemplates.PLAYABLE_WITH_MODIFIERS);

		// Instruct being repository to return the acting being when requested
		given(repository.findById(owningMudBeing.getCode()))
			.willReturn(Optional.of(owningMudBeing));
		
		// Instruct being repository to return other beings in the place when requested
		given(repository.findByCurWorldAndCurPlaceCode(owningMudBeing.getCurWorld(), owningMudBeing.getCurPlaceCode()))
			.willReturn(otherMudBeings);
		
		// Launch the notification!
		mockListener.receiveNotification(msg);
		
		// Preparing the message request to compare against
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_DROP_YOURS_MSG);
		yoursMsgRequest.setArgs(msg.getArgs());
		yoursMsgRequest.addChangedEntity(EnumEntityType.ITEM, msg.getEntityId());		
		
		// Check if proper message was sent to item's owner
		verify(messageService).putMessage(msg.getTargetEntityId(), yoursMsgRequest);
		
		MessageRequest anotherMsgRequest = (MessageRequest)SerializationUtils.clone(yoursMsgRequest);
		anotherMsgRequest.setMessageKey(BeingHelper.BEING_DROP_ANOTHER_MSG);
		anotherMsgRequest.setArgs(new String[] {
				owningMudBeing.getName(),
				msg.getArgs()[0]
		});
		
		// Check if proper message was sent to other beings as well
		otherMudBeings.stream()
			.filter(d -> d.getPlayerId()!=null)
			.forEach(d -> 
				verify(messageService).putMessage(d.getCode(), anotherMsgRequest)
					);
		
	}
	
	@Test
	public void testItemTakenNotificationReceived() {
		
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(ItemNotificationTemplates.ITEM_TAKEN);
		
		// Being owning the item
		MudBeing owningMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		// Adjusting the being code to reflect the item.owner
		owningMudBeing.setCode(msg.getTargetEntityId());
		
		// Instruct being repository to return the acting being when requested
		given(repository.findById(owningMudBeing.getCode()))
			.willReturn(Optional.of(owningMudBeing));
		
		// Launch the notification!
		mockListener.receiveNotification(msg);
		
		// Preparing the message request to compare against
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_TAKE_YOURS_MSG);
		yoursMsgRequest.setArgs(msg.getArgs());
		yoursMsgRequest.addChangedEntity(EnumEntityType.ITEM, msg.getEntityId());		
		
		
		// Check if proper message was sent to item's owner
		verify(messageService).putMessage(msg.getTargetEntityId(), yoursMsgRequest);
	}
	
	@Test
	public void testOwnedItemQttyIncreasedNotificationReceived() {
		
		testOwnedNotification(ItemNotificationTemplates.ITEM_OWNED_QTTY_INCREASE);
	}	

	@Test
	public void testOwnedItemQttyDecreasedNotificationReceived() {
		
		testOwnedNotification(ItemNotificationTemplates.ITEM_OWNED_QTTY_DECREASE);
	}

	@Test
	public void testOwnedItemAttrChangedNotificationReceived() {
		
		testOwnedNotification(ItemNotificationTemplates.ITEM_OWNED_ATTR_CHANGE);
	}

	@Test
	public void testOwnedItemDestroyedNotificationReceived() {
		
		testOwnedNotification(ItemNotificationTemplates.ITEM_OWNED_DESTROY);
	}

	@Test
	public void testOwnedItemClassChangedNotificationReceived() {
		
		testOwnedNotification(ItemNotificationTemplates.ITEM_OWNED_CLASS_CHANGE);
	}
	
	
	@Test
	public void testUnOwnedItemQttyIncreasedNotificationReceived() {

		testUnownedNotification(ItemNotificationTemplates.ITEM_UNOWNED_QTTY_INCREASE);
	}	

	@Test
	public void testUnOwnedItemQttyDecreasedNotificationReceived() {
		
		testUnownedNotification(ItemNotificationTemplates.ITEM_UNOWNED_QTTY_DECREASE);
	}

	@Test
	public void testUnOwnedItemAttrChangedNotificationReceived() {
		
		testUnownedNotification(ItemNotificationTemplates.ITEM_UNOWNED_ATTR_CHANGE);
	}

	@Test
	public void testUnOwnedItemDestroyedNotificationReceived() {
		
		testUnownedNotification(ItemNotificationTemplates.ITEM_UNOWNED_DESTROY);
	}

	@Test
	public void testUnOwnedItemClassChangedNotificationReceived() {
		
		testUnownedNotification(ItemNotificationTemplates.ITEM_UNOWNED_CLASS_CHANGE);
	}
	
	private void testOwnedNotification(String label) {

		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(label);
		
		// Being owning the item
		MudBeing owningMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		// Adjusting the being code to reflect the item.owner
		owningMudBeing.setCode(msg.getTargetEntityId());
		
		// Instruct being repository to return the acting being when requested
		given(repository.findById(owningMudBeing.getCode()))
			.willReturn(Optional.of(owningMudBeing));

		// Launch the notification!
		mockListener.receiveNotification(msg);
		
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(msg.getMessageKey());
		yoursMsgRequest.setArgs(msg.getArgs());
		yoursMsgRequest.addChangedEntity(EnumEntityType.ITEM, msg.getEntityId());
		
		// Check if proper message was sent to item's owner
		verify(messageService).putMessage(msg.getTargetEntityId(), yoursMsgRequest);
	}
	
	private void testUnownedNotification(String label) {
		
		NotificationMessage msg = Fixture.from(NotificationMessage.class)
				.gimme(label);
		
		// Beings in the same place
		List<MudBeing> otherMudBeings = 
				Fixture.from(MudBeing.class).gimme(3, 
						BeingTemplates.SIMPLE, 
						BeingTemplates.PLAYABLE, 
						BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct being repository to return other beings in the place when requested
		given(repository.findByCurWorldAndCurPlaceCode(msg.getWorldName(), msg.getTargetEntityId().intValue()))
			.willReturn(otherMudBeings);
		
		// Launch the notification!
		mockListener.receiveNotification(msg);
		
		MessageRequest placeMsgRequest = new MessageRequest();
		placeMsgRequest.setMessageKey(msg.getMessageKey());
		placeMsgRequest.setArgs(msg.getArgs());
		placeMsgRequest.addChangedEntity(EnumEntityType.ITEM, msg.getEntityId());
		
		// Check if proper message was sent to other beings
		otherMudBeings.stream()
			.filter(d -> d.getPlayerId()!=null)
			.forEach(d -> 
				verify(messageService).putMessage(d.getCode(), placeMsgRequest)
					);
	}
	
}
