package com.jpinfo.mudengine.being;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.being.client.MessageServiceClient;
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.fixture.MudBeingProcessor;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.converter.BeingConverter;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.service.BeingServiceImpl;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.security.TokenService;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BeingTests {
	
	private static final Integer HP_ATTR_CHANGE_VALUE=500;
	private static final Integer HP_ATTR_ZEROES_VALUE=0;
	
	private static final Integer MAXHP_ATTR_VALUE=100;
	
	private static final Long CREATE_BEING_ID = 99L;
	
	// This mock bean isn't used during validation.
	// It's mocked just to avoid having it trying to call outside world
	@MockBean
	private MessageServiceClient mockMessage;
	
	@MockBean
	private TokenService tokenService;
	
	@MockBean
	private BeingRepository mockRepository;
	
	@MockBean
	private BeingClassRepository mockClassRepository;
	
	@Autowired
	private BeingServiceImpl service;

	@PostConstruct
	private void setup() {
		FixtureFactoryLoader.loadTemplates("com.jpinfo.mudengine.being.fixture");
		
		given(mockClassRepository.findById(ArgumentMatchers.anyString()))
		.willAnswer(i -> {
			
			return Optional.of(
					BeingTestData.loadMudBeingClass(i.getArgument(0, String.class))
					);
		});

		given(mockRepository.findById(ArgumentMatchers.anyLong()))
		.willAnswer(i -> {
			
			return Optional.of(
					BeingTestData.loadMudBeing(i.getArgument(0, Long.class))
					);
		});
		
		given(mockRepository.save(ArgumentMatchers.any(MudBeing.class)))
		.willAnswer(i -> {
			
			MudBeing beingSaved = i.getArgument(0, MudBeing.class);
			
			// Is it being created?
			if (beingSaved.getCode()==null) {
				
				// Assign a random code
				beingSaved.setCode(BeingTests.CREATE_BEING_ID);
				
			} 
			return beingSaved;
		});

	}
	
	@Test
	public void testCreateSimple() throws IOException {
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.SIMPLE);
		
		MudBeingClass originalMudBeingClass = BeingTestData.loadMudBeingClass(BeingTestData.MUD_ORIGINAL_BEING_CLASS);
		
		Being serviceBeing =
				service.createBeing(Being.enumBeingType.REGULAR_NON_SENTIENT, 
						BeingTestData.MUD_ORIGINAL_BEING_CLASS,
						originalMudBeing.getCurWorld(),
						originalMudBeing.getCurPlaceCode(),
						originalMudBeing.getQuantity(), 
						originalMudBeing.getName());
		
		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeingClass, serviceBeing.getBeingClass());
		
		// Check consistency between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());
	}
	
	@Test
	public void testCreatePlayable() throws IOException {
		MudBeing originalMudBeing = Fixture.from(MudBeing.class)
				.uses(new MudBeingProcessor())
				.gimme(BeingTemplates.PLAYABLE);
		
		MudBeingClass originalMudBeingClass = BeingTestData.loadMudBeingClass(BeingTestData.MUD_ORIGINAL_BEING_CLASS);
		
		Being serviceBeing =
				service.createPlayerBeing(originalMudBeing.getPlayerId(),
						BeingTestData.MUD_ORIGINAL_BEING_CLASS,
						originalMudBeing.getCurWorld(), 
						originalMudBeing.getCurPlaceCode(), 
						originalMudBeing.getName()
						);

		assertThat(serviceBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(serviceBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(serviceBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(serviceBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
		assertThat(serviceBeing.getName()).isEqualTo(originalMudBeing.getName());
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeingClass, serviceBeing.getBeingClass());
		
		// Check consistence between class attributes and being attributes
		assertAttrMap(serviceBeing, serviceBeing.getBeingClass());
		assertSkillMap(serviceBeing, serviceBeing.getBeingClass());

	}
	
	@Test
	public void testUpdateClass() throws IOException {
		
		Being toChangeBeing = BeingConverter.convert(
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID)
				);
		
		toChangeBeing.setClassCode(BeingTestData.MUD_CHANGED_BEING_CLASS);
		
		MudBeingClass newMudBeingClass = BeingTestData.loadMudBeingClass(BeingTestData.MUD_CHANGED_BEING_CLASS);
		
		
		Being changedBeing = 
				service.updateBeing(BeingTestData.READ_BEING_ID, toChangeBeing);
		
		// Check being class (including attr maps between database and service)
		assertBeingClass(newMudBeingClass, changedBeing.getBeingClass());

		// Check consistence between class attributes and being attributes
		assertAttrMap(newMudBeingClass, changedBeing.getBeingClass());
		assertSkillMap(changedBeing, changedBeing.getBeingClass());
	}

	@Test
	public void testUpdateHPBeyondMax() throws IOException {
		
		Being toChangeBeing = BeingConverter.convert(
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID)
				);
		
		toChangeBeing.getBaseAttrs().put(
				BeingHelper.BEING_HP_ATTR, 
				BeingTests.HP_ATTR_CHANGE_VALUE);
		
		Being changedBeing = 
				service.updateBeing(BeingTestData.READ_BEING_ID, toChangeBeing);
		
		assertThat(changedBeing.getAttrs().get(BeingHelper.BEING_HP_ATTR)).isEqualTo(BeingTests.MAXHP_ATTR_VALUE);
	}
	
	@Test
	public void testUpdateHPToZero() throws IOException {
		
		SecurityContextHolder.getContext().setAuthentication(
				tokenService.getAuthenticationFromToken(tokenService.buildInternalToken())
				);
		
		Being toChangeBeing = BeingConverter.convert(
				BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID)
				);
		
		toChangeBeing.getBaseAttrs().put(
				BeingHelper.BEING_HP_ATTR, 
				BeingTests.HP_ATTR_ZEROES_VALUE);
		
		service.updateBeing(BeingTestData.READ_BEING_ID, toChangeBeing);
		
		verify(mockRepository, times(1)).deleteById(BeingTestData.READ_BEING_ID);
	}

	@Test
	public void testDestroy() {
		
		service.destroyBeing(BeingTestData.READ_BEING_ID);

		verify(mockRepository, times(1)).deleteById(BeingTestData.READ_BEING_ID);
	}
	
	@Test
	public void testRead() throws IOException {
		
		MudBeing originalMudBeing = BeingTestData.loadMudBeing(BeingTestData.READ_BEING_ID);

		Being serviceBeing = service.getBeing(BeingTestData.READ_BEING_ID);
		
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
