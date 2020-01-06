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
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.service.NotificationBeingService;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.security.TokenService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeingAttrNotificationTests {
	
	private static final String NEW_ATTRMOD = "ATTRMOD";
	private static final float NEW_ATTRMOD_VALUE = 5;
	
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
	public void testAttrIncreaseNotification() throws IOException {

		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		MudBeingAttrModifier newAttrModifier = afterBeing.getAttrModifiers().iterator().next();
		newAttrModifier.setOffset(newAttrModifier.getOffset() + CHANGED_OFFSET_VALUE);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				newAttrModifier.getId().getCode(),
				String.valueOf(CHANGED_OFFSET_VALUE)
		});
		

		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
	
	
	
	@Test
	public void testAttrDecreaseNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		MudBeingAttrModifier newAttrModifier = afterBeing.getAttrModifiers().iterator().next();
		newAttrModifier.setOffset(newAttrModifier.getOffset() - CHANGED_OFFSET_VALUE);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				newAttrModifier.getId().getCode(),
				String.valueOf(CHANGED_OFFSET_VALUE)
		});
		

		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testAttrRemovedNotification() throws Throwable {

		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding skill modifier on the first one
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		newModifier.setId(new MudBeingAttrModifierPK());
		newModifier.getId().setCode(BeingAttrNotificationTests.NEW_ATTRMOD);
		newModifier.setOffset(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE);
		
		beforeBeing.getAttrModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingAttrNotificationTests.NEW_ATTRMOD,
				String.valueOf(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
	

	@Test
	public void testAttrAddedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding skill modifier on the second one
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		newModifier.setId(new MudBeingAttrModifierPK());
		newModifier.getId().setCode(BeingAttrNotificationTests.NEW_ATTRMOD);
		newModifier.setOffset(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE);
		
		afterBeing.getAttrModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingAttrNotificationTests.NEW_ATTRMOD,
				String.valueOf(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testNegativeAttrRemovedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding negative skill modifier on the first one
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		newModifier.setId(new MudBeingAttrModifierPK());
		newModifier.getId().setCode(BeingAttrNotificationTests.NEW_ATTRMOD);
		newModifier.setOffset(-BeingAttrNotificationTests.NEW_ATTRMOD_VALUE);
		
		beforeBeing.getAttrModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingAttrNotificationTests.NEW_ATTRMOD,
				String.valueOf(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}

	@Test
	public void testNegativeAttrAddedNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing beforeBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		// Clone it to have a second, unattached instance
		MudBeing afterBeing = (MudBeing)SerializationUtils.clone(beforeBeing);
		
		// Adding negative skill modifier on the second one
		MudBeingAttrModifier newModifier = new MudBeingAttrModifier();
		newModifier.setId(new MudBeingAttrModifierPK());
		newModifier.getId().setCode(BeingAttrNotificationTests.NEW_ATTRMOD);
		newModifier.setOffset(-BeingAttrNotificationTests.NEW_ATTRMOD_VALUE);
		
		afterBeing.getAttrModifiers().add(newModifier);

		// Launch and check
		List<MessageRequest> messages = 
				service.handleBeingChange(beforeBeing, afterBeing);
		
		service.dispatchMessages(messages, afterBeing);
		
		// Preparing the expected message result
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, afterBeing.getCode());
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_ATTRMOD_DECREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				BeingAttrNotificationTests.NEW_ATTRMOD,
				String.valueOf(BeingAttrNotificationTests.NEW_ATTRMOD_VALUE)
		});
		
		// Check if correct message was sent
		verify(messageService).putMessage(afterBeing.getCode(), yoursMsgRequest);
	}
}
