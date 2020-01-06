package com.jpinfo.mudengine.being;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.service.NotificationBeingService;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeingDestroyNotificationTests {
	
	@MockBean
	private BeingRepository repository;
	
	@MockBean
	private MessageServiceClient messageService;

	@MockBean
	private JmsTemplate jmsMockTemplate;
	
	@Autowired
	private NotificationBeingService service;
	
	@MockBean
	private TokenService tokenUtils;
	
	List<MudBeing> otherMudBeings;
	
	@PostConstruct
	private void setup() throws IOException {
		
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
		
		// Beings in the same place
		otherMudBeings = 
				Fixture.from(MudBeing.class).gimme(3, 
						BeingTemplates.SIMPLE, 
						BeingTemplates.PLAYABLE, 
						BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Add the user being destroyed as well
		otherMudBeings.add( 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID)
				);
		
		// Instruct being repository to return other beings in the place when requested
		given(repository.findPlayableInThisPlace(
				ArgumentMatchers.any(),
				ArgumentMatchers.any()
				))
			.willReturn(otherMudBeings);
		
	}
	
	@Test
	public void testDestroyBeing() throws IOException {
		
		MudBeing destroyedBeing = 
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);
		
		service.handleDestroyedBeing(destroyedBeing);
		
		// Preparing the expected message result
		MessageRequest destroyYoursMsg = new MessageRequest();
		destroyYoursMsg.addChangedEntity(EnumEntityType.BEING, BeingTestData.READ_BEING_ID);
		destroyYoursMsg.setMessageKey(BeingHelper.BEING_DESTROY_YOURS_MSG);
		destroyYoursMsg.setArgs(new String[] {
				destroyedBeing.getName()
		});

		// Check if message was sent to original being
		verify(messageService).putMessage(BeingTestData.READ_BEING_ID, destroyYoursMsg);
		
		// Prepare the expected message for other beings
		MessageRequest destroyAnotherMsg = new MessageRequest();
		destroyAnotherMsg.addChangedEntity(EnumEntityType.BEING, BeingTestData.READ_BEING_ID);
		destroyAnotherMsg.setMessageKey(BeingHelper.BEING_DESTROY_ANOTHER_MSG);
		destroyAnotherMsg.setArgs(new String[] {
				destroyedBeing.getName()
		});
		
		
		// Check if proper message was sent to other beings as well
		otherMudBeings.stream()
			.filter(d -> !d.getCode().equals(BeingTestData.READ_BEING_ID))
			.forEach(d -> 
				verify(messageService).putMessage(d.getCode(), destroyAnotherMsg)
					);

		// Prepare the expected notification
		NotificationMessage beingNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.BEING)
				.entityId(BeingTestData.READ_BEING_ID.longValue())
				.event(EnumNotificationEvent.BEING_DESTROY)
			.build();		
		
		verify(jmsMockTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(beingNotification), 
				ArgumentMatchers.any(MessagePostProcessor.class));
	}

}
