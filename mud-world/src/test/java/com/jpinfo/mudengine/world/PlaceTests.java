package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
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
	
	private static final String HP_ATTR = "HP";
	private static final Integer HP_ATTR_CHANGED_VALUE = 5;
	private static final Integer HP_ATTR_ZEROES_VALUE = 0;
	private static final Integer HP_ATTR_ABOVE_VALUE = 5000;
	
	private static final String MAXHP_ATTR = "MAXHP";
	private static final Integer MAXHP_ATTR_CHANGED_VALUE = 530;
	private static final Integer MAXHP_ATTR_VALUE = 500;
	
	private static final String OTHER_ATTR = "OTH";
	private static final Integer OTHER_ATTR_CHANGED_VALUE = 8;

	private static final Integer CREATE_PLACE_ID = 99;
	private static final String CREATE_PLACE_CLASS = "TEST";
	private static final String CREATE_PLACE_EXIT_DIRECTION = "UP";
	private static final Integer CREATE_PLACE_EXIT_TARGET = 1;
	
	private static final Integer READ_PLACE_ID = 1;
	
	private static final String UPDATE_CLASS_PLACE_CLASS = "TESTBLDG";
	
	private static final Integer DELETE_DEMISED_PLACE_ID = 6;
	private static final String DELETE_DEMISED_PLACE_CLASS = "RUIN";
	

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
	public void testCreatePlace() throws IOException {
		
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

		MudPlaceClass mudPlaceClass = loadMudPlaceClass(PlaceTests.CREATE_PLACE_CLASS);
		
		// Checking if all attrs from mudclass are present
		assertThat(mudPlaceClass.getAttrs().stream()
				.allMatch(curClassAttr -> 
					createdPlace.getAttrs().containsKey(curClassAttr.getCode()) &&
					createdPlace.getAttrs().get(curClassAttr.getCode()).equals(curClassAttr.getValue())
				)
				).isTrue();
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
	public void testReadPlace() throws IOException {
		
		Place responsePlace = service.getPlace(PlaceTests.READ_PLACE_ID);
		
		MudPlace dbPlace = loadMudPlace(PlaceTests.READ_PLACE_ID);

		// Checking basic fields		
		assertThat(responsePlace.getPlaceClass().getPlaceClassCode())
			.isEqualTo(dbPlace.getPlaceClass().getCode());
		
		// Checking the exits
		assertThat(dbPlace.getExits().stream()
				.allMatch(curExit -> 
					responsePlace.getExits().containsKey(curExit.getDirection())
					&&
					responsePlace.getExits().get(curExit.getDirection()).getTargetPlaceCode().equals(curExit.getTargetPlaceCode())
					)
				).isTrue();
		
		// Checking the attributes
		MudPlaceClass mudPlaceClass = loadMudPlaceClass(responsePlace.getPlaceClass().getPlaceClassCode());
		
		// Checking if all attrs from mudclass are present
		assertThat(mudPlaceClass.getAttrs().stream()
				.allMatch(curClassAttr -> 
					responsePlace.getAttrs().containsKey(curClassAttr.getCode()) &&
					responsePlace.getAttrs().get(curClassAttr.getCode()).equals(curClassAttr.getValue())
					)
				).isTrue();
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
	
	@Test
	public void testUpdateHPAboveMAXHP() {
		
		Place originalPlace = service.getPlace(PlaceTests.READ_PLACE_ID);
		
		// Update the HP
		originalPlace.getAttrs().put(PlaceTests.HP_ATTR, PlaceTests.HP_ATTR_ABOVE_VALUE);
		
		Place responsePlace = service.updatePlace(PlaceTests.READ_PLACE_ID, originalPlace);
		
		// Checking the attributes
		// In this case, the first attribute (HP) need to have this value set to MAXHP
		assertThat(responsePlace.getAttrs().get(PlaceTests.HP_ATTR)).isEqualTo(MAXHP_ATTR_VALUE);
	}

	@Test
	public void testUpdateHPBelowZero() {
		
		Place originalPlace = service.getPlace(PlaceTests.READ_PLACE_ID);
		
		// Update the HP
		originalPlace.getAttrs().put(PlaceTests.HP_ATTR, PlaceTests.HP_ATTR_ZEROES_VALUE);
		
		service.updatePlace(PlaceTests.READ_PLACE_ID, originalPlace);
		
		verify(mockRepository).deleteById(PlaceTests.READ_PLACE_ID);
		
		/*
		//  Check if correct notification was sent
		NotificationMessage placeNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.UPDATE_DESTROYED_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_DESTROY)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeNotification), 
				ArgumentMatchers.any());

		*/
	}

	@Test
	public void testUpdateAttributes() {
		
		Place originalPlace = service.getPlace(PlaceTests.READ_PLACE_ID);
		
		// Updating the attributes
		originalPlace.getAttrs().put(PlaceTests.HP_ATTR, PlaceTests.HP_ATTR_CHANGED_VALUE);
		originalPlace.getAttrs().put(PlaceTests.MAXHP_ATTR, PlaceTests.MAXHP_ATTR_CHANGED_VALUE);
		originalPlace.getAttrs().put(PlaceTests.OTHER_ATTR, PlaceTests.OTHER_ATTR_CHANGED_VALUE);
		
		Place responsePlace = service.updatePlace(PlaceTests.READ_PLACE_ID, originalPlace);
		
		// Checking the attributes
		assertThat(responsePlace.getAttrs().get(PlaceTests.HP_ATTR)).isEqualTo(PlaceTests.HP_ATTR_CHANGED_VALUE);
		assertThat(responsePlace.getAttrs().get(PlaceTests.MAXHP_ATTR)).isEqualTo(PlaceTests.MAXHP_ATTR_CHANGED_VALUE);
		assertThat(responsePlace.getAttrs().get(PlaceTests.OTHER_ATTR)).isEqualTo(PlaceTests.OTHER_ATTR_CHANGED_VALUE);
	}

	@Test
	public void testDeleteDemised() throws IOException {
		
		// Prepare the expected entity to be persisted
		MudPlace expectedDemisedPlace = loadMudPlace(PlaceTests.DELETE_DEMISED_PLACE_ID);
		expectedDemisedPlace.setPlaceClass(loadMudPlaceClass(DELETE_DEMISED_PLACE_CLASS));
		expectedDemisedPlace.setAttrs(new HashSet<>());
		
		service.destroyPlace(PlaceTests.DELETE_DEMISED_PLACE_ID);
		
		verify(mockRepository).save(expectedDemisedPlace);
	}
	
/*
		
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
	*/
	
	@Test
	public void testDelete() {
		
		service.destroyPlace(PlaceTests.READ_PLACE_ID);
		
		verify(mockRepository).deleteById(PlaceTests.READ_PLACE_ID);

		/*
		
		//  Check if correct notification was sent
		NotificationMessage placeNotification = NotificationMessage.builder()
				.entity(NotificationMessage.EnumEntity.PLACE)
				.entityId(PlaceTests.DELETE_PLACE_ID.longValue())
				.event(EnumNotificationEvent.PLACE_DESTROY)
			.build();
		
		verify(jmsTemplate).convertAndSend((Destination)ArgumentMatchers.any(), 
				ArgumentMatchers.eq(placeNotification), 
				ArgumentMatchers.any());
				
		*/

	}
	
}
