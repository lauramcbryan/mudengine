package com.jpinfo.mudengine.being;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
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

import com.jpinfo.mudengine.being.client.ItemServiceClient;
import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.fixture.MudBeingProcessor;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.notification.NotificationAspect;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.message.MessageEntity.EnumEntityType;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"being.exchange=" + BeingNotificationTests.BEING_EXCHANGE})
public class BeingNotificationTests {
	
	public static final String BEING_EXCHANGE = "being.exchange";

	private static final String SKILL="TEST_SKILL";
	private static final String ATTR="TEST_ATTR";
	
	private static final float BIG_OFFSET_VALUE = 100.0f;
	private static final float SMALL_OFFSET_VALUE = 10.0f;

	// This mock bean isn't used during validation.
	// It's mocked just to avoid having it trying to call outside world
	@MockBean
	private ItemServiceClient mockItem;
	
	@MockBean
	private BeingRepository repository;
	
	@Autowired
	private NotificationAspect aspect;
	
	@MockBean
	private MessageServiceClient messageService;

	@MockBean
	private RabbitTemplate rabbit;
	
	@MockBean
	private ProceedingJoinPoint pjp;
	
	
	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
	}	
	
	@Test
	public void testSkillIncreaseNotification() throws Throwable {
		
		// Get a mudBeing with modifiers
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Get some random modifier (the first one, in fact) and set it's value
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(SMALL_OFFSET_VALUE);
		
		// Instruct the repository to return this being (the original)
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clone it to have a second, unattached instance
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// find the modifier and changed it's value
		changedMudBeing.getSkillModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(BIG_OFFSET_VALUE));
		
		// Launch and check
		testNotification(changedMudBeing, originalSkillMod.getId().getCode(), 
				(BIG_OFFSET_VALUE - SMALL_OFFSET_VALUE), 
				BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
	}
	
	@Test
	public void testSkillDecreaseNotification() throws Throwable {
		
		// Get a MudBeing with modifiers
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Pick a random modifier and set it's value
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(BIG_OFFSET_VALUE);
		
		// Instruct the repository to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clone it
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// change modifier
		changedMudBeing.getSkillModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(SMALL_OFFSET_VALUE));

		// Launch and check
		testNotification(changedMudBeing, originalSkillMod.getId().getCode(), 
				(BIG_OFFSET_VALUE - SMALL_OFFSET_VALUE), 
				BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
	}
	
	@Test
	public void testSkillRemovedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Set the value for the first modifier
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(SMALL_OFFSET_VALUE);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// removes the skill modifier
		changedMudBeing.getSkillModifiers()
			.removeIf(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()));
		
		
		// Launch and check
		testNotification(changedMudBeing, originalSkillMod.getId().getCode(), 
				SMALL_OFFSET_VALUE, 
				BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
	}
	

	
	@Test
	public void testSkillAddedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		MudBeingSkillModifierPK newModifierPK = new MudBeingSkillModifierPK();
		
		newModifierPK.setBeingCode(originalMudBeing.getCode());
		newModifierPK.setCode(BeingNotificationTests.SKILL);
		
		newModifier.setId(newModifierPK);
		newModifier.setOffset(BIG_OFFSET_VALUE);
		
		// adds the skill modifier
		changedMudBeing.getSkillModifiers().add(newModifier);
		
		// Launch and check
		testNotification(changedMudBeing, newModifier.getId().getCode(), 
				BIG_OFFSET_VALUE, 
				BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
	}

	@Test
	public void testNegativeSkillRemovedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Set the value for the first modifier
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(-SMALL_OFFSET_VALUE);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// removes the skill modifier
		changedMudBeing.getSkillModifiers()
			.removeIf(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()));
		
		
		// Launch and check
		testNotification(changedMudBeing, originalSkillMod.getId().getCode(), 
				SMALL_OFFSET_VALUE, 
				BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
	}

	@Test
	public void testNegativeSkillAddedNotification() throws Throwable {
		
		// Creates a test being
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		// Instruct the database to return this original being
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		// Clones the first being into another being
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		MudBeingSkillModifier newModifier = new MudBeingSkillModifier();
		MudBeingSkillModifierPK newModifierPK = new MudBeingSkillModifierPK();
		
		newModifierPK.setBeingCode(originalMudBeing.getCode());
		newModifierPK.setCode(BeingNotificationTests.SKILL);
		
		newModifier.setId(newModifierPK);
		newModifier.setOffset(-BIG_OFFSET_VALUE);
		
		// adds the skill modifier
		changedMudBeing.getSkillModifiers().add(newModifier);
		
		// Launch and check
		testNotification(changedMudBeing, newModifier.getId().getCode(), 
				BIG_OFFSET_VALUE, 
				BeingHelper.BEING_SKILLMOD_DECREASE_MSG);
	}
	
	
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
		given(repository.findByCurWorldAndCurPlaceCode(destroyedMudBeing.getCurWorld(), destroyedMudBeing.getCurPlaceCode()))
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
		verify(rabbit).convertAndSend(BEING_EXCHANGE, "", beingNotification);
		
		
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

}
