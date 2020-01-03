package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;

import org.mockito.Mockito;
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.common.utils.NotificationMessage;
import com.jpinfo.mudengine.common.utils.NotificationMessage.EnumNotificationEvent;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.MudPlaceClassAttrPK;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.repository.PlaceExitRepository;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.service.PlaceServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceTests {
	
	private static final String MUD_PLACE_PREFIX = "src/test/resources/mudplace-";
	private static final String MUD_PLACE_SUFFIX = ".json";
	
	private static final String MUD_PLACE_CLASS_PREFIX = "src/test/resources/mudplaceclass-";
	private static final String MUD_PLACE_CLASS_SUFFIX = ".json";
	
	
	public static final String PLACE_EXCHANGE = "place.exchange";

	private static final Integer CREATE_PLACE_ID = 99;
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

	@MockBean
	private JmsTemplate jmsTemplate;
	
	@MockBean
	private PlaceRepository mockRepository;
	
	@MockBean
	private PlaceExitRepository mockExitRepository;
	
	@MockBean
	private PlaceClassRepository mockClassRepository;
	
	@MockBean
	private TokenService tokenUtils;
	
	@Autowired
	private PlaceServiceImpl service;
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	private MudPlaceClass loadMudPlaceClass(String className) throws IOException{
		
		return jsonMapper.readValue(new File(
				PlaceTests.MUD_PLACE_CLASS_PREFIX +
				className + 
				PlaceTests.MUD_PLACE_CLASS_SUFFIX
				), MudPlaceClass.class);
	}
	
	private MudPlace loadMudPlace(Integer placeId) throws IOException{
		
		return jsonMapper.readValue(new File(
				PlaceTests.MUD_PLACE_PREFIX +
				placeId + 
				PlaceTests.MUD_PLACE_SUFFIX
				), MudPlace.class);
	}
	
	
	@PostConstruct
	public void setup() throws IOException {
		
		given(mockClassRepository.findById(ArgumentMatchers.anyString()))
			.willAnswer(i -> {
				
				return Optional.of(
						loadMudPlaceClass(i.getArgument(0, String.class))
						);
			});
		
		given(mockRepository.findById(ArgumentMatchers.anyInt()))
			.willAnswer(i -> {
				
				return Optional.of(
						loadMudPlace(i.getArgument(0, Integer.class))
						);
			});
		
		given(mockRepository.save(ArgumentMatchers.any(MudPlace.class)))
		.willAnswer(i -> {
			
			MudPlace placeBeingSaved = i.getArgument(0, MudPlace.class);
			
			// Is it being created?
			if (placeBeingSaved.getCode()==null) {
				
				// Assign a random code
				placeBeingSaved.setCode(PlaceTests.CREATE_PLACE_ID);
				
			} 
			
			return placeBeingSaved;
		});
	}
	
	@Test
	public void testCreatePlace() {
		
		Place createdPlace = 
				service.createPlace(
						PlaceTests.CREATE_PLACE_CLASS, 
						PlaceTests.CREATE_PLACE_EXIT_DIRECTION,
						PlaceTests.CREATE_PLACE_EXIT_TARGET);

		// Checking the placeClass
		assertThat(createdPlace.getPlaceClass().getPlaceClassCode())
			.isEqualTo(PlaceTests.CREATE_PLACE_CLASS);

		//Check if exit was created and if it points to the right direction
		assertThat(createdPlace.getExits().get(PlaceTests.CREATE_PLACE_EXIT_DIRECTION)).isNotNull();
		assertThat(createdPlace.getExits().get(PlaceTests.CREATE_PLACE_EXIT_DIRECTION).getTargetPlaceCode())
			.isEqualTo(PlaceTests.CREATE_PLACE_EXIT_TARGET);

		// Checking the attributes
		for (int index=0;index<PlaceTests.CREATE_PLACE_CLASS_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.CREATE_PLACE_CLASS_ATTRS[index];
			Integer curAttrValue = PlaceTests.CREATE_PLACE_CLASS_ATTR_VALUES[index];
			
			assertThat(createdPlace.getAttrs().get(curAttr)).isNotNull();
			assertThat(createdPlace.getAttrs().get(curAttr)).isEqualTo(curAttrValue);
		}
		
		/*
		//  Check if correct notification was sent
		NotificationMessage placeExitNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(responseCreate.getBody().getCode().longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_CREATE)
			.build();
		
		NotificationMessage placeExit2Notification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.CREATE_PLACE_EXIT_TARGET.longValue())
				.event(EnumNotificationEvent.PLACE_EXIT_CREATE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExitNotification), 
				ArgumentMatchers.any());
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeExit2Notification), 
				ArgumentMatchers.any());
				
				*/
		
	}
	
	@Test
	public void testReadPlace() {
		
		Place responsePlace = service.getPlace(PlaceTests.READ_PLACE_ID);

		// Checking basic fields		
		assertThat(responsePlace.getPlaceClass().getPlaceClassCode()).isEqualTo(PlaceTests.READ_PLACE_CLASS);
		
		// Checking if the exit exists		
		assertThat(responsePlace.getExits().containsKey(PlaceTests.READ_PLACE_EXIT_DIRECTION)).isTrue();
		
		// Check if the exit points to the right place
		assertThat(responsePlace.getExits().get(PlaceTests.READ_PLACE_EXIT_DIRECTION).getTargetPlaceCode())
			.isEqualTo(PlaceTests.READ_PLACE_EXIT_TARGET);
		
		
		// Checking the attributes
		for (int index=0;index<PlaceTests.READ_PLACE_CLASS_ATTRS.length;index++) {
			
			String curAttr = PlaceTests.READ_PLACE_CLASS_ATTRS[index];
			Integer curAttrValue = PlaceTests.READ_PLACE_CLASS_ATTR_VALUES[index];

			// Check if attribute exists
			assertThat(responsePlace.getAttrs().containsKey(curAttr)).isTrue();
			
			// Check if it has the correct value			
			assertThat(responsePlace.getAttrs().get(curAttr)).isEqualTo(curAttrValue);
		}
	}
	
	@Test
	public void testUpdateClass() throws IOException {
		
		Place originalPlace = service.getPlace(PlaceTests.READ_PLACE_ID);
		
		// Change the placeClass
		originalPlace.setClassCode(PlaceTests.UPDATE_CLASS_PLACE_CLASS);
		
		Place changedPlace = service.updatePlace(PlaceTests.READ_PLACE_ID, originalPlace);
		
		// Load the placeClass from file to check changes
		MudPlaceClass changedPlaceClass = loadMudPlaceClass(PlaceTests.UPDATE_CLASS_PLACE_CLASS);
		
		// Check if the placeClass has changed accordingly
		assertThat(changedPlace.getClassCode()).isEqualTo(changedPlaceClass.getCode());

		// Checking if all attributes exist in updated place
		assertThat(changedPlaceClass.getAttrs().stream()
				.allMatch(curClassAttr -> 
					changedPlace.getAttrs().containsKey(curClassAttr.getCode()) &&
					changedPlace.getAttrs().get(curClassAttr.getCode()).equals(curClassAttr.getValue())
				)
				).isTrue();

		/*
		//  Check if correct notification was sent
		NotificationMessage placeClassNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(responsePlace.getCode().longValue())
				.event(EnumNotificationEvent.PLACE_CLASS_CHANGE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeClassNotification), 
				ArgumentMatchers.any());
				
				*/
		
	}
	
	/*

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
		
		//  Check if correct notification was sent
		NotificationMessage placeNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.UPDATE_DESTROYED_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_DESTROY)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeNotification), 
				ArgumentMatchers.any());

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

		
		//  Check if correct notification was sent
		NotificationMessage placeClassNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.DELETE_DEMISED_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_CLASS_CHANGE)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeClassNotification), 
				ArgumentMatchers.any());

		
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
		
		//  Check if correct notification was sent
		NotificationMessage placeNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.DELETE_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_DESTROY)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeNotification), 
				ArgumentMatchers.any());

	}
	*/
	
}
