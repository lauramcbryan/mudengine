package com.jpinfo.mudengine.being;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.*;

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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.being.client.ItemServiceClient;
import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.fixture.MudBeingProcessor;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.converter.BeingConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingAttrConverter;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.notification.NotificationAspect;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT,
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"being.exchange=" + BeingTests.BEING_EXCHANGE})
public class BeingTests {
	
	public static final String BEING_EXCHANGE = "being.exchange";
	
	private static final Integer MAX_HP=100;
	private static final Integer HP=200;
	
	private static final String SKILL="TEST_SKILL";
	private static final String ATTR="TEST_ATTR";
	
	private static final float BIG_OFFSET_VALUE = 100.0f;
	private static final float SMALL_OFFSET_VALUE = 10.0f;
	
	
	@MockBean
	private ItemServiceClient mockItem;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TokenService tokenService;
	
	@MockBean
	private BeingRepository repository;
	
	@MockBean
	private BeingClassRepository classRepository;
	
	@MockBean
	private MessageServiceClient messageService;

	@MockBean
	private RabbitTemplate rabbit;
	
	@MockBean
	private ProceedingJoinPoint pjp;
	
	@Autowired
	private NotificationAspect aspect;
	
	private HttpEntity<Object> emptyHttpEntity;
	

	@PostConstruct
	private void setup() {
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, tokenService.buildInternalToken());
		
		emptyHttpEntity = new HttpEntity<Object>(authHeaders);
		
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
	}
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testCreateSimple() {
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);

		MudBeing cleanWithIdMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		cleanWithIdMudBeing.getAttrs().clear();
		cleanWithIdMudBeing.getSkills().clear();
		cleanWithIdMudBeing.getSlots().clear();
		
		MudBeing cleanMudBeing = (MudBeing)SerializationUtils.clone(cleanWithIdMudBeing);
		cleanMudBeing.setCode(null);
		
		given(repository.save(cleanMudBeing)).willReturn(cleanWithIdMudBeing);
		given(repository.save(originalMudBeing)).willReturn(originalMudBeing);
		
		given(classRepository.findById(originalMudBeing.getBeingClass().getCode())).willReturn(Optional.of(originalMudBeing.getBeingClass()));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("beingType", Being.enumBeingType.REGULAR_NON_SENTIENT);
		urlVariables.put("beingClass", originalMudBeing.getBeingClass().getCode());
		urlVariables.put("worldName", originalMudBeing.getCurWorld());
		urlVariables.put("placeCode", originalMudBeing.getCurPlaceCode());
		urlVariables.put("beingName", originalMudBeing.getName());
		urlVariables.put("quantity", originalMudBeing.getQuantity());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being?beingType={beingType}&beingClass={beingClass}&worldName={worldName}&placeCode={placeCode}&beingName={beingName}&quantity={quantity}", 
				HttpMethod.PUT, emptyHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseService.getBody()).isNotNull();
		
		Being serviceBeing = responseService.getBody();
		
		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeing.getBeingClass(), serviceBeing.getBeingClass());
		
		// Check consistency between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());
		
		
	}
	
	@Test
	public void testCreatePlayable() {
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);

		MudBeing cleanWithIdMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		cleanWithIdMudBeing.getAttrs().clear();
		cleanWithIdMudBeing.getSkills().clear();
		cleanWithIdMudBeing.getSlots().clear();
		
		MudBeing cleanMudBeing = (MudBeing)SerializationUtils.clone(cleanWithIdMudBeing);
		cleanMudBeing.setCode(null);
		
		given(repository.save(cleanMudBeing)).willReturn(cleanWithIdMudBeing);
		given(repository.save(originalMudBeing)).willReturn(originalMudBeing);
		
		given(classRepository.findById(originalMudBeing.getBeingClass().getCode())).willReturn(Optional.of(originalMudBeing.getBeingClass()));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("playerId", originalMudBeing.getPlayerId());
		urlVariables.put("beingClass", originalMudBeing.getBeingClass().getCode());
		urlVariables.put("worldName", originalMudBeing.getCurWorld());
		urlVariables.put("placeCode", originalMudBeing.getCurPlaceCode());
		urlVariables.put("beingName", originalMudBeing.getName());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being/player/{playerId}?beingClass={beingClass}&worldName={worldName}&placeCode={placeCode}&beingName={beingName}", 
				HttpMethod.PUT, emptyHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseService.getBody()).isNotNull();
		
		Being serviceBeing = responseService.getBody();
		
		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
		assertThat(serviceBeing.getName()).isEqualTo(originalMudBeing.getName());
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeing.getBeingClass(), serviceBeing.getBeingClass());
		
		// Check consistence between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());

	}

	@Test
	public void testUpdateHP() {

		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		originalMudBeing.getAttrs().add(
				MudBeingAttrConverter.build(
						originalMudBeing.getCode(), 
						BeingHelper.BEING_HP_ATTR, 
						BeingTests.MAX_HP));
		
		originalMudBeing.getAttrs().add(
				MudBeingAttrConverter.build(
						originalMudBeing.getCode(), 
						BeingHelper.BEING_MAX_HP_ATTR, 
						BeingTests.MAX_HP));
		
		
		Being originalBeing = BeingConverter.convert(originalMudBeing);
		
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		given(repository.save(originalMudBeing)).willReturn(originalMudBeing);
		
		originalBeing.getBaseAttrs().put(BeingHelper.BEING_HP_ATTR, BeingTests.HP);
		HttpEntity<Being> beingHttpEntity = new HttpEntity<Being>(originalBeing, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<>();
		
		urlVariables.put("beingCode", originalMudBeing.getCode());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being/{beingCode}", 
				HttpMethod.POST, beingHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseService.getBody().getAttrs().get(BeingHelper.BEING_HP_ATTR)).isEqualTo(BeingTests.MAX_HP);
	}
	
	@Test
	public void testUpdateDestroyed() {
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		originalMudBeing.getAttrs().add(
				MudBeingAttrConverter.build(
						originalMudBeing.getCode(), 
						BeingHelper.BEING_HP_ATTR, 
						BeingTests.MAX_HP));
		
		originalMudBeing.getAttrs().add(
				MudBeingAttrConverter.build(
						originalMudBeing.getCode(), 
						BeingHelper.BEING_MAX_HP_ATTR, 
						BeingTests.MAX_HP));
		
		Being originalBeing = BeingConverter.convert(originalMudBeing);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		originalBeing.getBaseAttrs().put(BeingHelper.BEING_HP_ATTR, 0);
		HttpEntity<Being> beingHttpEntity = new HttpEntity<Being>(originalBeing, emptyHttpEntity.getHeaders());
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("beingCode", originalMudBeing.getCode());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being/{beingCode}", 
				HttpMethod.POST, beingHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);

		verify(repository, times(1)).delete(originalMudBeing);
	}

	@Test
	public void testDestroy() {

		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("beingCode", originalMudBeing.getCode());

		ResponseEntity<Void> responseService= restTemplate.exchange(
				"/being/{beingCode}", 
				HttpMethod.DELETE, emptyHttpEntity, Void.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);

		verify(repository, times(1)).delete(originalMudBeing);
	}
	
	@Test
	public void testDestroyAllFromPlace() {
		
		MudBeing firstMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		MudBeing secondMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);

		MudBeing thirdMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		given(repository.findByCurWorldAndCurPlaceCode(firstMudBeing.getCurWorld(), firstMudBeing.getCurPlaceCode()))
			.willReturn(Arrays.asList(firstMudBeing, secondMudBeing, thirdMudBeing));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("worldName", firstMudBeing.getCurWorld());
		urlVariables.put("placeCode", firstMudBeing.getCurPlaceCode());

		ResponseEntity<Void> responseService= restTemplate.exchange(
				"/being/place/{worldName}/{placeCode}", 
				HttpMethod.DELETE, emptyHttpEntity, Void.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);

		verify(repository, times(1)).delete(firstMudBeing);
		verify(repository, times(1)).delete(secondMudBeing);
		verify(repository, times(1)).delete(thirdMudBeing);
		
	}
	
	@Test
	public void testRead() {
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("beingCode", originalMudBeing.getCode());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being/{beingCode}", 
				HttpMethod.GET, emptyHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseService.getBody()).isNotNull();
		
		Being serviceBeing = responseService.getBody();
		
		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeing.getBeingClass(), serviceBeing.getBeingClass());
		
		// Check consistency between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());


		
	}
	
	@Test
	public void testReadFromAnotherPlayer() {
		
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, tokenService.buildInternalToken(2L));
		
		HttpEntity<Object> anotherHttpEntity = new HttpEntity<Object>(authHeaders);

		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		given(repository.findById(originalMudBeing.getCode())).willReturn(Optional.of(originalMudBeing));
		
		Map<String, Object> urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("beingCode", originalMudBeing.getCode());

		ResponseEntity<Being> responseService= restTemplate.exchange(
				"/being/{beingCode}", 
				HttpMethod.GET, anotherHttpEntity, Being.class, urlVariables);
		
		assertThat(responseService.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseService.getBody()).isNotNull();
		
		Being serviceBeing = responseService.getBody();
		
		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(originalMudBeing.getQuantity());
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeing.getBeingClass(), serviceBeing.getBeingClass());
		
		// Check consistency between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());
		
		assertThat(serviceBeing.getAttrModifiers()).isEmpty();
		assertThat(serviceBeing.getSkillModifiers()).isEmpty();
		
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
		newModifierPK.setCode(BeingTests.SKILL);
		
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
		newModifierPK.setCode(BeingTests.SKILL);
		
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
		newModifierPK.setCode(BeingTests.ATTR);
		
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
		newModifierPK.setCode(BeingTests.ATTR);
		
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
	
	
	private void assertAttrMap(MudBeingClass mudBeingClass, BeingClass beingClass) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(beingClass.getAttrs().keySet().size()).isEqualTo(mudBeingClass.getAttrs().size());
		
		// Checking that all attributes in database response are present in result
		for(MudBeingClassAttr curAttr: mudBeingClass.getAttrs()) {
			assertThat(beingClass.getAttrs()).containsKey(curAttr.getId().getCode());
			assertThat(beingClass.getAttrs().get(curAttr.getId().getCode())).isEqualTo(curAttr.getValue());
		}		
	}

	private void assertAttrMap(Being being, BeingClass beingClass) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(being.getAttrs().keySet().size()).isEqualTo(beingClass.getAttrs().keySet().size());
		
		// Checking that all attributes in database response are present in result
		for(String curAttr: beingClass.getAttrs().keySet()) {
			assertThat(being.getAttrs()).containsKey(curAttr);
			assertThat(being.getAttrs().get(curAttr)).isEqualTo(beingClass.getAttrs().get(curAttr));
		}		
	}
	
	private void assertSkillMap(MudBeingClass mudBeingClass, BeingClass beingClass) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(beingClass.getSkills().keySet().size()).isEqualTo(mudBeingClass.getSkills().size());
		
		// Checking that all attributes in database response are present in result
		for(MudBeingClassSkill curSkill: mudBeingClass.getSkills()) {
			assertThat(beingClass.getSkills()).containsKey(curSkill.getId().getCode());
			assertThat(beingClass.getSkills().get(curSkill.getId().getCode())).isEqualTo(curSkill.getValue());
		}		
	}

	private void assertSkillMap(Being being, BeingClass beingClass) {

		// Ensure that the attributes map has the same size as returned by database
		assertThat(being.getSkills().keySet().size()).isEqualTo(beingClass.getSkills().keySet().size());
		
		// Checking that all attributes in database response are present in result
		for(String curSkill: beingClass.getSkills().keySet()) {
			assertThat(being.getSkills()).containsKey(curSkill);
			assertThat(being.getSkills().get(curSkill)).isEqualTo(beingClass.getSkills().get(curSkill));
		}		
	}
	
	
	
	private void assertBeingClass(MudBeingClass mudBeingClass, BeingClass beingClass) {

		assertThat(beingClass).isNotNull();
		assertThat(beingClass.getCode()).isEqualTo(mudBeingClass.getCode());
		assertThat(beingClass.getName()).isEqualTo(mudBeingClass.getName());
		assertThat(beingClass.getSize()).isEqualTo(mudBeingClass.getSize());
		assertThat(beingClass.getWeightCapacity()).isEqualTo(mudBeingClass.getWeightCapacity());
		assertThat(beingClass.getDescription()).isEqualTo(mudBeingClass.getDescription());

		// Ensure that the attributes map has the same size as returned by database
		assertThat(beingClass.getAttrs().keySet().size()).isEqualTo(mudBeingClass.getAttrs().size());
		
		// Checking that all attributes in database response are present in result		
		assertAttrMap(mudBeingClass, beingClass);
		
		// Checking that all skills in database response are present in result		
		assertSkillMap(mudBeingClass, beingClass);
	}
}
