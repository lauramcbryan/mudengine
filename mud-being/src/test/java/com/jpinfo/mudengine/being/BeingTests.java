package com.jpinfo.mudengine.being;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.*;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import com.jpinfo.mudengine.being.fixture.BeingTemplates;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;

import static org.mockito.BDDMockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class BeingTests {
	
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
		
		MudBeing originalMudBeing = Fixture.from(MudBeing.class).gimme(BeingTemplates.SIMPLE);
		originalMudBeing.setQuantity(BeingHelper.CREATE_DEFAULT_QUANTITY);
		originalMudBeing.setType(Being.enumBeingType.REGULAR_NON_SENTIENT.ordinal());

		MudBeing cleanWithIdMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		cleanWithIdMudBeing.getAttrs().clear();
		cleanWithIdMudBeing.getSkills().clear();
		cleanWithIdMudBeing.getSlots().clear();
		
		MudBeing cleanMudBeing = (MudBeing)SerializationUtils.clone(cleanWithIdMudBeing);
		cleanMudBeing.setCode(null);
		
		/*
		MudBeing updatedMudBeing = (MudBeing)SerializationUtils.clone(originalMudBeing);
		updatedMudBeing.getAttrs().clear();
		updatedMudBeing.getSkills().clear();
		updatedMudBeing.getSlots().clear();
		 */
		
		
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

		ResponseEntity<Being> responseCreate= restTemplate.exchange(
				"/being?beingType={beingType}&beingClass={beingClass}&worldName={worldName}&placeCode={placeCode}&beingName={beingName}&quantity={quantity}", 
				HttpMethod.PUT, emptyHttpEntity, Being.class, urlVariables);
		
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseCreate.getBody()).isNotNull();
		
		Being createdBeing = responseCreate.getBody();
		
		assertThat(createdBeing.getType()).isEqualTo(Being.enumBeingType.values()[originalMudBeing.getType()]);
		assertThat(createdBeing.getCurWorld()).isEqualTo(originalMudBeing.getCurWorld());
		assertThat(createdBeing.getCurPlaceCode()).isEqualTo(originalMudBeing.getCurPlaceCode());
		assertThat(createdBeing.getQuantity()).isEqualTo(BeingHelper.CREATE_DEFAULT_QUANTITY);
	
		// Check being class (including attr maps between database and service)
		assertBeingClass(originalMudBeing.getBeingClass(), createdBeing.getBeingClass());
		
		// Check consistence between class attributes and being attributes
		assertAttrMap(createdBeing, createdBeing.getBeingClass());
		assertSkillMap(createdBeing, createdBeing.getBeingClass());
	}
	
	public void testCreatePlayable() {
		
	}
	
	public void testUpdateHP() {
		
	}
	
	public void testUpdateDestroyed() {
		
	}
	
	public void testDestroy() {
		
	}
	
	public void testDestroyAllFromPlace() {
		
	}
	
	public void testRead() {
		
	}
	
	public void testReadFromAnotherPlayer() {
		
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
