package com.jpinfo.mudengine.being;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(SMALL_OFFSET_VALUE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// increase skill
		changedMudBeing.getSkillModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(BIG_OFFSET_VALUE));
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		MessageRequest yoursMsgRequest = new MessageRequest();
		yoursMsgRequest.setMessageKey(BeingHelper.BEING_SKILLMOD_INCREASE_MSG);
		yoursMsgRequest.setArgs(new String[] {
				originalSkillMod.getId().getCode()
		});
		yoursMsgRequest.addChangedEntity(EnumEntityType.BEING, originalMudBeing.getCode());
		
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_INCREASE_MSG), eq(originalSkillMod.getId().getCode()),
				anyString());

	}
	
	@Test
	public void testSkillDecreaseNotification() throws Throwable {
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE_WITH_MODIFIERS);
		
		MudBeingSkillModifier originalSkillMod = originalMudBeing.getSkillModifiers().iterator().next();
		originalSkillMod.setOffset(BIG_OFFSET_VALUE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		
		MudBeing changedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		
		// increase skill
		changedMudBeing.getSkillModifiers().stream()
			.filter(d -> d.getId().getCode().equals(originalSkillMod.getId().getCode()))
			.findFirst()
			.ifPresent(d -> d.setOffset(SMALL_OFFSET_VALUE));
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_DECREASE_MSG), eq(originalSkillMod.getId().getCode()),
				anyString());

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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_DECREASE_MSG), eq(originalSkillMod.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_INCREASE_MSG), eq(newModifier.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_INCREASE_MSG), eq(originalSkillMod.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_SKILLMOD_DECREASE_MSG), eq(newModifier.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_INCREASE_MSG), eq(originalAttrMod.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_DECREASE_MSG), eq(originalAttrMod.getId().getCode()),
				anyString());

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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_DECREASE_MSG), eq(originalAttrMod.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_INCREASE_MSG), eq(newModifier.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_INCREASE_MSG), eq(originalAttrMod.getId().getCode()),
				anyString());
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
		
		
		aspect.compareBeing(pjp, changedMudBeing);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(originalMudBeing.getCode()), 
				eq(BeingHelper.BEING_ATTRMOD_DECREASE_MSG), eq(newModifier.getId().getCode()),
				anyString());
	}

	
	@Test
	public void testDestroyNotification() throws Throwable {
		
		// Creates a test being
		MudBeing destroyedMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		
		aspect.sendDestroyNotification(pjp, destroyedMudBeing);
		
		NotificationMessage beingNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.BEING)
				.entityId(destroyedMudBeing.getCode())
				.event(EnumNotificationEvent.BEING_DESTROY)
				.build();
		
		// Check if correct notification was sent		
		verify(rabbit).convertAndSend(BEING_EXCHANGE, "", beingNotification);
		
		// Check if correct message was created
		verify(messageService).putMessage(eq(destroyedMudBeing.getCode()), 
				eq(BeingHelper.BEING_DESTROY_YOURS_MSG));
	}
}
