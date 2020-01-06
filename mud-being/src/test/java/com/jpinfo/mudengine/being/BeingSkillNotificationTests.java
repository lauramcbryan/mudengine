package com.jpinfo.mudengine.being;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.service.NotificationBeingService;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.security.TokenService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeingSkillNotificationTests {
	
	private static final String NEW_SKILLMOD = "SKILLMOD";
	private static final float NEW_SKILLMOD_VALUE = 5;
	
	private static final float CHANGED_OFFSET_VALUE = 10.0f;
	
	@MockBean
	private MessageServiceClient messageService;

	@MockBean
	private JmsTemplate jmsMockTemplate;
	
	@Autowired
	private NotificationBeingService service;
	
	@MockBean
	private TokenService tokenUtils;
	
	@Test
	public void testSkillIncreaseNotification() throws IOException {

		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		MudBeingSkillModifier newSkillModifier = afterBeing.getSkillModifiers().iterator().next();
		newSkillModifier.setOffset(newSkillModifier.getOffset() + CHANGED_OFFSET_VALUE);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				newSkillModifier.getId().getCode(),
				String.valueOf(CHANGED_OFFSET_VALUE)
		});
		

		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
	
	
	
	@Test
	public void testSkillDecreaseNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		MudBeingSkillModifier newSkillModifier = afterBeing.getSkillModifiers().iterator().next();
		newSkillModifier.setOffset(newSkillModifier.getOffset() - CHANGED_OFFSET_VALUE);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				newSkillModifier.getId().getCode(),
				String.valueOf(CHANGED_OFFSET_VALUE)
		});
		

		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testSkillRemovedNotification() throws Throwable {

		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding skill modifier on the first one
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		newModifier.setId(new MudBeingSkillModifierPK());
		newModifier.getId().setCode(BeingSkillNotificationTests.NEW_SKILLMOD);
		newModifier.setOffset(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE);
		
		beforeBeing.getSkillModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingSkillNotificationTests.NEW_SKILLMOD,
				String.valueOf(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
	

	@Test
	public void testSkillAddedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding skill modifier on the second one
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		newModifier.setId(new MudBeingSkillModifierPK());
		newModifier.getId().setCode(BeingSkillNotificationTests.NEW_SKILLMOD);
		newModifier.setOffset(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE);
		
		afterBeing.getSkillModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingSkillNotificationTests.NEW_SKILLMOD,
				String.valueOf(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testNegativeSkillRemovedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding negative skill modifier on the first one
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		newModifier.setId(new MudBeingSkillModifierPK());
		newModifier.getId().setCode(BeingSkillNotificationTests.NEW_SKILLMOD);
		newModifier.setOffset(-BeingSkillNotificationTests.NEW_SKILLMOD_VALUE);
		
		beforeBeing.getSkillModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingSkillNotificationTests.NEW_SKILLMOD,
				String.valueOf(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testNegativeSkillAddedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding negative skill modifier on the second one
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		newModifier.setId(new MudBeingSkillModifierPK());
		newModifier.getId().setCode(BeingSkillNotificationTests.NEW_SKILLMOD);
		newModifier.setOffset(-BeingSkillNotificationTests.NEW_SKILLMOD_VALUE);
		
		afterBeing.getSkillModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingSkillNotificationTests.NEW_SKILLMOD,
				String.valueOf(BeingSkillNotificationTests.NEW_SKILLMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
	

	/*
	@Test
	public void testAttrIncreaseNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Set the value for the first modifier
		MudBeingAttrModifier originalAttrMod = originalMudBeing.getAttrModifiers().iterator().next();
		originalAttrMod.setOffset(SMALL_OFFSET_VALUE);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// increase attr in the second being
		changedMudBeing.getAttrModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalAttrMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(BIG_OFFSET_VALUE));
		

		// Launch and check
		testNotification(changedMudBeing, originalAttrMod.getId().getCode(), 
				(BIG_OFFSET_VALUE - SMALL_OFFSET_VALUE), 
				BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
	}
	
	@Test
	public void testAttrDecreaseNotification() throws Throwable {
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		MudBeingAttrModifier originalAttrMod = originalMudBeing.getAttrModifiers().iterator().next();
		originalAttrMod.setOffset(BIG_OFFSET_VALUE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// increase attr
		changedMudBeing.getAttrModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalAttrMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(SMALL_OFFSET_VALUE));
		
		
		// Launch and check
		testNotification(changedMudBeing, originalAttrMod.getId().getCode(), 
				(BIG_OFFSET_VALUE - SMALL_OFFSET_VALUE), 
				BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
	}
	
	
	@Test
	public void testAttrRemovedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Set the value for the first modifier
		MudBeingAttrModifier originalAttrMod = originalMudBeing.getAttrModifiers().iterator().next();
		originalAttrMod.setOffset(SMALL_OFFSET_VALUE);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// removes the attr modifier
		changedMudBeing.getAttrModifiers()
			.removeIf(d -> d.getId().getCode().equals(originalAttrMod.getId().getCode()));
		

		// Launch and check
		testNotification(changedMudBeing, originalAttrMod.getId().getCode(), 
				SMALL_OFFSET_VALUE, 
				BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
	}
	

	
	@Test
	public void testAttrAddedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		MudBeingAttrModifierPK newModifierPK = new MudBeingAttrModifierPK();
		
		newModifierPK.setBeingCode(originalMudBeing.getCode());
		newModifierPK.setCode(BeingNotificationTests.ATTR);
		
		newModifier.setId(newModifierPK);
		newModifier.setOffset(BIG_OFFSET_VALUE);
		
		// adds the attr modifier
		changedMudBeing.getAttrModifiers().add(newModifier);
		
		// Launch and check
		testNotification(changedMudBeing, newModifier.getId().getCode(), 
				BIG_OFFSET_VALUE, 
				BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
	}
	
	@Test
	public void testNegativeAttrRemovedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Set the value for the first modifier
		MudBeingAttrModifier originalAttrMod = originalMudBeing.getAttrModifiers().iterator().next();
		originalAttrMod.setOffset(-SMALL_OFFSET_VALUE);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// removes the attr modifier
		changedMudBeing.getAttrModifiers()
			.removeIf(d -> d.getId().getCode().equals(originalAttrMod.getId().getCode()));
		
		// Launch and check
		testNotification(changedMudBeing, originalAttrMod.getId().getCode(), 
				SMALL_OFFSET_VALUE, 
				BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
	}
	

	
	@Test
	public void testNegativeAttrAddedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		MudBeingAttrModifierPK newModifierPK = new MudBeingAttrModifierPK();
		
		newModifierPK.setBeingCode(originalMudBeing.getCode());
		newModifierPK.setCode(BeingNotificationTests.ATTR);
		
		newModifier.setId(newModifierPK);
		newModifier.setOffset(-BIG_OFFSET_VALUE);
		
		// adds the attr modifier
		changedMudBeing.getAttrModifiers().add(newModifier);
		
		// Launch and check
		testNotification(changedMudBeing, newModifier.getId().getCode(), 
				BIG_OFFSET_VALUE, 
				BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
	}

	
	@Test
	public void testDestroyNotification() throws Throwable {
		
		// Creates a test being
		MudBeing destroyedMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		// Create another beings and put them in the same place
		List<MudBeing> anotherMudBeings = Fixture.from(MudBeing.class)
				.gimme(3, BeingTemplates.PLAYABLE, BeingTemplates.SIMPLE, BeingTemplates.PLAYABLE);
		
		// Instruct the repository to return the other beings in place
		given(repository.findPlayableInThisPlace(destroyedMudBeing.getCurWorld(), destroyedMudBeing.getCurPlaceCode()))
			.willReturn(anotherMudBeings);
		
		// Launch the notification
		aspect.sendDestroyNotification(pjp, destroyedMudBeing);
		
		// Check the message sent
		NotificationMessage beingNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.BEING)
				.entityId(destroyedMudBeing.getCode())
				.event(EnumNotificationEvent.BEING_DESTROY)
				.build();
		
		// Check if correct notification was sent		
		verify(jmsMockTemplate).convertAndSend(
				ArgumentMatchers.eq(new ActiveMQTopic(BeingTests.BEING_EXCHANGE)), 
				ArgumentMatchers.eq(beingNotification), 
				ArgumentMatchers.any());
		
		
		// Preparing the expected message result for the destroyed being
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_DESTROY_YOURS_MSG);
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, destroyedMudBeing.getCode());

		// Check if correct message was sent
		verify(messageService).putMessage(destroyedMudBeing.getCode(), yoursMsgRequest);
		
		// Preparing the expected message result for other beings
		MessageRequest anotherMsgRequest = new MessageRequest();
		anotherMsgRequest.setMessageKey(BeingHelper.BEING_DESTROY_ANOTHER_MSG);
		anotherMsgRequest.setArgs(new String[] {
				destroyedMudBeing.getName()
		});
		anotherMsgRequest.addChangedEntity(EnumEntityType.BEING, destroyedMudBeing.getCode());

		// Check if correct message was sent
		anotherMudBeings.stream()
			.filter(d -> d.getPlayerId()!=null)
			.forEach(d ->
			verify(messageService).putMessage(d.getCode(), anotherMsgRequest)
			);
	}
	
	private void testNotification(MudBeing changedMudBeing, String changedModifier, float changedValue, String messageKey) throws Throwable {
		
		// Launch the notification
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(messageKey);
		yoursMsgRequest.setArgs(new String[] {
				changedModifier,
				String.valueOf(changedValue)
		});
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, changedMudBeing.getCode());

		// Check if correct message was sent
		verify(messageService).putMessage(changedMudBeing.getCode(), yoursMsgRequest);

	}
	*/

}
