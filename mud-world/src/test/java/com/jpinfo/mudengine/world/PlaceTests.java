package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import javax.annotation.PostConstruct;

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

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.world.client.BeingServiceClient;
import com.jpinfo.mudengine.world.client.ItemServiceClient;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.repository.PlaceRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, 
	properties= {"token.secret=a7ac498c7bba59e0eb7c647d2f0197f8",
			"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"})
public class PlaceTests {
	

	private static final String CREATE_PLACE_CLASS = "TEST";
	private static final String CREATE_PLACE_EXIT_DIRECTION = "UP";
	private static final Integer CREATE_PLACE_EXIT_TARGET = 1;
	private static final String[] CREATE_PLACE_CLASS_ATTRS= {"HP", "MAXHP"};
	private static final Integer[] CREATE_PLACE_CLASS_ATTR_VALUES= {50, 500};
	
	private static final Integer READ_PLACE_ID = 1;
	private static final String READ_PLACE_CLASS = "TEST";
	private static final String READ_PLACE_EXIT_DIRECTION = "OUT";
	private static final Integer READ_PLACE_EXIT_TARGET = 2;
	private static final String[] READ_PLACE_CLASS_ATTRS= {"HP", "MAXHP"};
	private static final Integer[] READ_PLACE_CLASS_ATTR_VALUES= {50, 100};
	
	
	private static final Integer UPDATE_CLASS_PLACE_ID = 2;
	private static final String UPDATE_CLASS_PLACE_CLASS = "TESTBLDG";
	private static final String[] UPDATE_CLASS_PLACE_ATTRS= {"HP2", "MAXH2"};
	private static final Integer[] UPDATE_CLASS_PLACE_ATTR_VALUES= {3, 8};

	private static final Integer UPDATE_HP_PLACE_ID = 3;
	private static final String UPDATE_HP_PLACE_CLASS = "TEST";
	private static final String[] UPDATE_HP_PLACE_ATTRS= {"HP", "MAXHP"};
	private static final Integer[] UPDATE_HP_PLACE_ATTR_VALUES= {150, 100};

	private static final Integer UPDATE_DESTROYED_PLACE_ID = 4;
	private static final String[] UPDATE_DESTROYED_PLACE_ATTRS= {"HP", "MAXHP"};

	private static final Integer UPDATE_ATTR_PLACE_ID = 5;
	private static final String[] UPDATE_ATTR_PLACE_ATTRS= {"HP", "MAXHP", "HP3"};
	private static final Integer[] UPDATE_ATTR_PLACE_ATTR_VALUES= {1, 8, 3};
	
	
	private static final Integer DELETE_DEMISED_PLACE_ID = 6;
	private static final String DELETE_DEMISED_PLACE_CLASS = "RUIN";
	
	
	private static final Integer DELETE_PLACE_ID = 7;

	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private PlaceRepository repository;
	
	@Autowired
	private TokenService tokenUtils;
	
	@MockBean
	private ItemServiceClient mockItem;
	
	@MockBean
	private BeingServiceClient mockBeing;

	
	private HttpEntity<Object> authEntity;
	
	private HttpHeaders authHeaders;
	
	@PostConstruct
	public void setup() {

		// Creating the authentication token		
		authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, tokenUtils.buildInternalToken());
		
		authEntity = new HttpEntity<>(authHeaders);
	}
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void testCreatePlace() {
		
		// *********** CREATE **********
		// =============================
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("placeClassCode", PlaceTests.CREATE_PLACE_CLASS);
		urlVariables.put("direction", PlaceTests.CREATE_PLACE_EXIT_DIRECTION);
		urlVariables.put("targetPlaceCode", PlaceTests.CREATE_PLACE_EXIT_TARGET);
			
		ResponseEntity<Place> responseCreate= restTemplate.exchange(
				"/place/?placeClassCode={placeClassCode}&direction={direction}&targetPlaceCode={targetPlaceCode}", 
				HttpMethod.PUT, authEntity, Place.class, urlVariables);
		
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		MudPlace dbPlace = repository.findById(responseCreate.getBody().getCode())
				.orElseThrow(() -> new RuntimeException("Created place not found in database"));
		
		
		assertThat(dbPlace.getPlaceClass().getCode()).isEqualTo(PlaceTests.CREATE_PLACE_CLASS);
		
		// Retrieving the exit
		MudPlaceExit exit =
			dbPlace.getExits().stream()
				.filter(d -> d.getPk().getDirection().equals(PlaceTests.CREATE_PLACE_EXIT_DIRECTION))
				.findFirst()
				.orElseGet(() -> null);
		
		// Checking if the exit exists		
		assertThat(exit).isNotNull();
		
		// Check if the exit points to the right direction
		assertThat(exit.getTargetPlaceCode()).isEqualTo(PlaceTests.CREATE_PLACE_EXIT_TARGET);
		
		// Checking the attributes
		for (int index=0;index<PlaceTests.CREATE_PLACE_CLASS_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.CREATE_PLACE_CLASS_ATTRS[index];
			Integer curAttrValue = PlaceTests.CREATE_PLACE_CLASS_ATTR_VALUES[index];

			MudPlaceAttr attr = 
					dbPlace.getAttrs().stream()
						.filter(d -> d.getId().getCode().equals(curAttr))
						.findFirst()
						.orElse(null);
			
			// Check if attribute exists
			assertThat(attr).isNotNull();
			
			// Check if it has the correct value
			assertThat(attr.getValue()).isEqualTo(curAttrValue);
		}
		
	}
	
	@Test
	public void testReadPlace() {

		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.READ_PLACE_ID);

		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseGet.getBody()).isNotNull();
		
		Place responsePlace = responseGet.getBody();

		// Checking basic fields		
		assertThat(responsePlace.getPlaceClass().getPlaceClassCode()).isEqualTo(PlaceTests.READ_PLACE_CLASS);
		
		// Checking if the exit exists		
		assertThat(responsePlace.getExits().get(PlaceTests.READ_PLACE_EXIT_DIRECTION)).isNotNull();
		
		// Check if the exit points to the right place
		assertThat(responsePlace.getExits().get(PlaceTests.READ_PLACE_EXIT_DIRECTION).getTargetPlaceCode())
			.isEqualTo(PlaceTests.READ_PLACE_EXIT_TARGET);
		
		
		// Checking the attributes
		for (int index=0;index<PlaceTests.READ_PLACE_CLASS_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.READ_PLACE_CLASS_ATTRS[index];
			Integer curAttrValue = PlaceTests.READ_PLACE_CLASS_ATTR_VALUES[index];

			// Check if attribute exists
			assertThat(responsePlace.getAttrs().get(curAttr)).isNotNull();
			
			// Check if it has the correct value			
			assertThat(responsePlace.getAttrs().get(curAttr)).isEqualTo(curAttrValue);
		}
	}
		
	@Test
	public void testUpdateClass() {
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.UPDATE_CLASS_PLACE_ID);
		
		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseGet.getBody()).isNotNull();
		
		Place originalPlace = responseGet.getBody();
		
		// Change the placeClass
		originalPlace.setClassCode(PlaceTests.UPDATE_CLASS_PLACE_CLASS);
		
		//dummyPlace.getAttrs().put(PlaceTests.extraPlaceAttr, new Integer(1));
		
		HttpEntity<Place> request = new HttpEntity<Place>(originalPlace, authHeaders);
			
		ResponseEntity<Place> responseUpdate = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.POST, request, Place.class, urlVariables);
		
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseUpdate.getBody()).isNotNull();
		
		// Creating a Place variable to shorten further validations
		Place responsePlace = responseUpdate.getBody();
		
		// Check if the placeClass has changed accordingly
		assertThat(responsePlace.getClassCode()).isEqualTo(PlaceTests.UPDATE_CLASS_PLACE_CLASS);

		// Checking the attributes
		for (int index=0;index<PlaceTests.UPDATE_CLASS_PLACE_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.UPDATE_CLASS_PLACE_ATTRS[index];
			Integer curAttrValue = PlaceTests.UPDATE_CLASS_PLACE_ATTR_VALUES[index];

			// Check if attribute exists
			assertThat(responsePlace.getAttrs().get(curAttr)).isNotNull();
			
			// Check if it has the correct value			
			assertThat(responsePlace.getAttrs().get(curAttr)).isEqualTo(curAttrValue);
		}
		
	}

	@Test
	public void testUpdateHP() {
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.UPDATE_HP_PLACE_ID);
		
		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseGet.getBody()).isNotNull();
		
		Place originalPlace = responseGet.getBody();
		
		// Update the HP
		originalPlace.getAttrs().put(PlaceTests.UPDATE_HP_PLACE_ATTRS[0], PlaceTests.UPDATE_HP_PLACE_ATTR_VALUES[0]);
		
		HttpEntity<Place> request = new HttpEntity<Place>(originalPlace, authHeaders);
			
		ResponseEntity<Place> responseUpdate = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.POST, request, Place.class, urlVariables);
		
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseUpdate.getBody()).isNotNull();
		
		// Creating a Place variable to shorten further validations
		Place responsePlace = responseUpdate.getBody();
		
		// Check if the placeClass has changed accordingly
		assertThat(responsePlace.getClassCode()).isEqualTo(PlaceTests.UPDATE_HP_PLACE_CLASS);

		// Checking the attributes
		// In this case, the first attribute (HP) need to have this value set to MAXHP
		assertThat(responsePlace.getAttrs().get(UPDATE_HP_PLACE_ATTRS[0])).isNotNull();
		assertThat(responsePlace.getAttrs().get(UPDATE_HP_PLACE_ATTRS[0])).isEqualTo(UPDATE_HP_PLACE_ATTR_VALUES[1]);

		// Checking the second attribute as well
		assertThat(responsePlace.getAttrs().get(UPDATE_HP_PLACE_ATTRS[1])).isNotNull();
		assertThat(responsePlace.getAttrs().get(UPDATE_HP_PLACE_ATTRS[1])).isEqualTo(UPDATE_HP_PLACE_ATTR_VALUES[1]);
	}

	@Test
	public void testUpdateDestroyed() {

		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.UPDATE_DESTROYED_PLACE_ID);
		
		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseGet.getBody()).isNotNull();
		
		Place originalPlace = responseGet.getBody();
		
		// Update the HP to zero
		originalPlace.getAttrs().put(PlaceTests.UPDATE_DESTROYED_PLACE_ATTRS[0], 0);
		
		HttpEntity<Place> request = new HttpEntity<Place>(originalPlace, authHeaders);
			
		ResponseEntity<Place> responseUpdate = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.POST, request, Place.class, urlVariables);
		
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// Checking in the database if the place was destroyed
		assertThat(repository.findById(PlaceTests.UPDATE_DESTROYED_PLACE_ID).isPresent()).isFalse();
	}

	@Test
	public void testUpdateAttributes() {
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.UPDATE_ATTR_PLACE_ID);
		
		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseGet.getBody()).isNotNull();
		
		Place originalPlace = responseGet.getBody();
		
		// Updating the attributes
		originalPlace.getAttrs().put(UPDATE_ATTR_PLACE_ATTRS[0], UPDATE_ATTR_PLACE_ATTR_VALUES[0]);
		originalPlace.getAttrs().put(UPDATE_ATTR_PLACE_ATTRS[1], UPDATE_ATTR_PLACE_ATTR_VALUES[1]);
		originalPlace.getAttrs().put(UPDATE_ATTR_PLACE_ATTRS[2], UPDATE_ATTR_PLACE_ATTR_VALUES[2]);
		
		
		HttpEntity<Place> request = new HttpEntity<Place>(originalPlace, authHeaders);
			
		ResponseEntity<Place> responseUpdate = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.POST, request, Place.class, urlVariables);
		
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseUpdate.getBody()).isNotNull();
		
		// Creating a Place variable to shorten further validations
		Place responsePlace = responseUpdate.getBody();
		
		// Checking the attributes
		for (int index=0;index<PlaceTests.UPDATE_ATTR_PLACE_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.UPDATE_ATTR_PLACE_ATTRS[index];
			Integer curAttrValue = PlaceTests.UPDATE_ATTR_PLACE_ATTR_VALUES[index];

			// Check if attribute exists
			assertThat(responsePlace.getAttrs().get(curAttr)).isNotNull();
			
			// Check if it has the correct value			
			assertThat(responsePlace.getAttrs().get(curAttr)).isEqualTo(curAttrValue);
		}
		
	}

	@Test
	public void testDeleteDemised() {
		
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.DELETE_DEMISED_PLACE_ID);
		
		ResponseEntity<Place> responseFirstDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.DELETE, authEntity, Place.class, urlVariables);
		
		assertThat(responseFirstDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		MudPlace dbPlace = repository.findById(DELETE_DEMISED_PLACE_ID)
				.orElseThrow(() -> new RuntimeException("Demised place not found in database"));
		
		
		assertThat(dbPlace.getPlaceClass().getCode()).isEqualTo(PlaceTests.DELETE_DEMISED_PLACE_CLASS);
		
	}
	
	@Test
	public void testDelete() {
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>(); 
		urlVariables.put("placeId", PlaceTests.DELETE_PLACE_ID);
		
		// Deleting it again must remove the entity
		ResponseEntity<Place> responseSecondDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.DELETE, authEntity, Place.class, urlVariables);
		
		assertThat(responseSecondDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(repository.findById(DELETE_PLACE_ID).isPresent()).isFalse();
	}
	
}
