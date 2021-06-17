package com.jpinfo.mudengine.world.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.world.PlaceTestData;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.repository.PlaceRepository;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceImplTests {
	
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
	
	private static final Integer DELETE_DEMISED_PLACE_ID = 6;
	private static final String DELETE_DEMISED_PLACE_CLASS = "RUIN";

	@Mock
	private PlaceRepository placeRepository;
	
	@Mock
	private PlaceClassRepository placeClassRepository;
	
	@InjectMocks
	private PlaceServiceImpl service;
	
	
	@BeforeEach
	public void setup() throws IOException {
		
		Mockito.lenient().doAnswer(i -> {
			
			return Optional.of(
					PlaceTestData.loadMudPlaceClass(i.getArgument(0, String.class))
					);
		}).when(placeClassRepository).findById(ArgumentMatchers.anyString());
		
		Mockito.lenient().doAnswer(i -> {
			return Optional.of(
					PlaceTestData.loadMudPlace(i.getArgument(0, Integer.class))
					);
		}).when(placeRepository).findById(ArgumentMatchers.anyInt());
		
		Mockito.lenient().doAnswer(i -> {
			
			MudPlace placeBeingSaved = i.getArgument(0, MudPlace.class);
			
			// Is it being created?
			if (placeBeingSaved.getCode()==null) {
				
				// Assign a random code
				placeBeingSaved.setCode(PlaceServiceImplTests.CREATE_PLACE_ID);
				
			} 
			
			return placeBeingSaved;
		}).when(placeRepository).save(ArgumentMatchers.any(MudPlace.class));
	}
	
	@Test
	public void testCreatePlace() throws IOException {
		
		Place createdPlace = 
				service.createPlace(
						PlaceServiceImplTests.CREATE_PLACE_CLASS, 
						PlaceServiceImplTests.CREATE_PLACE_EXIT_DIRECTION,
						PlaceServiceImplTests.CREATE_PLACE_EXIT_TARGET);

		// Checking the placeClass
		assertThat(createdPlace.getPlaceClass().getPlaceClassCode())
			.isEqualTo(PlaceServiceImplTests.CREATE_PLACE_CLASS);

		//Check if exit was created and if it points to the right direction
		assertThat(createdPlace.getExits().get(PlaceServiceImplTests.CREATE_PLACE_EXIT_DIRECTION)).isNotNull();
		assertThat(createdPlace.getExits().get(PlaceServiceImplTests.CREATE_PLACE_EXIT_DIRECTION).getTargetPlaceCode())
			.isEqualTo(PlaceServiceImplTests.CREATE_PLACE_EXIT_TARGET);

		// Checking if all attrs from mudclass are present
		checkAttrMap(createdPlace, PlaceServiceImplTests.CREATE_PLACE_CLASS);
	}
	
	@Test
	public void testReadPlace() throws IOException {
		
		Place responsePlace = service.getPlace(PlaceTestData.READ_PLACE_ID);
		
		MudPlace dbPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);

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
		checkAttrMap(responsePlace, responsePlace.getPlaceClass().getPlaceClassCode());
	}
	
	@Test
	public void testUpdateClass() throws IOException {
		
		Place originalPlace = service.getPlace(PlaceTestData.READ_PLACE_ID);
		
		// Change the placeClass
		originalPlace.setClassCode(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
		Place changedPlace = service.updatePlace(PlaceTestData.READ_PLACE_ID, originalPlace);
		
		// Check if the placeClass has changed accordingly
		assertThat(changedPlace.getClassCode()).isEqualTo(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);

		// Checking if all attributes exist in updated place
		checkAttrMap(changedPlace, PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
	}
	
	@Test
	public void testUpdateHPAboveMAXHP() {
		
		Place originalPlace = service.getPlace(PlaceTestData.READ_PLACE_ID);
		
		// Update the HP
		originalPlace.getAttrs().put(PlaceServiceImplTests.HP_ATTR, PlaceServiceImplTests.HP_ATTR_ABOVE_VALUE);
		
		Place responsePlace = service.updatePlace(PlaceTestData.READ_PLACE_ID, originalPlace);
		
		// Checking the attributes
		// In this case, the first attribute (HP) need to have this value set to MAXHP
		assertThat(responsePlace.getAttrs().get(PlaceServiceImplTests.HP_ATTR)).isEqualTo(MAXHP_ATTR_VALUE);
	}

	@Test
	public void testUpdateHPBelowZero() {
		
		Place originalPlace = service.getPlace(PlaceTestData.READ_PLACE_ID);
		
		// Update the HP
		originalPlace.getAttrs().put(PlaceServiceImplTests.HP_ATTR, PlaceServiceImplTests.HP_ATTR_ZEROES_VALUE);
		
		service.updatePlace(PlaceTestData.READ_PLACE_ID, originalPlace);
		
		verify(placeRepository).deleteById(PlaceTestData.READ_PLACE_ID);
	}

	@Test
	public void testUpdateAttributes() {
		
		Place originalPlace = service.getPlace(PlaceTestData.READ_PLACE_ID);
		
		// Updating the attributes
		originalPlace.getAttrs().put(PlaceServiceImplTests.HP_ATTR, PlaceServiceImplTests.HP_ATTR_CHANGED_VALUE);
		originalPlace.getAttrs().put(PlaceServiceImplTests.MAXHP_ATTR, PlaceServiceImplTests.MAXHP_ATTR_CHANGED_VALUE);
		originalPlace.getAttrs().put(PlaceServiceImplTests.OTHER_ATTR, PlaceServiceImplTests.OTHER_ATTR_CHANGED_VALUE);
		
		Place responsePlace = service.updatePlace(PlaceTestData.READ_PLACE_ID, originalPlace);
		
		// Checking the attributes
		assertThat(responsePlace.getAttrs().get(PlaceServiceImplTests.HP_ATTR)).isEqualTo(PlaceServiceImplTests.HP_ATTR_CHANGED_VALUE);
		assertThat(responsePlace.getAttrs().get(PlaceServiceImplTests.MAXHP_ATTR)).isEqualTo(PlaceServiceImplTests.MAXHP_ATTR_CHANGED_VALUE);
		assertThat(responsePlace.getAttrs().get(PlaceServiceImplTests.OTHER_ATTR)).isEqualTo(PlaceServiceImplTests.OTHER_ATTR_CHANGED_VALUE);
	}

	@Test
	public void testDeleteDemised() throws IOException {
		
		// Prepare the expected entity to be persisted
		MudPlace expectedDemisedPlace = PlaceTestData.loadMudPlace(PlaceServiceImplTests.DELETE_DEMISED_PLACE_ID);
		expectedDemisedPlace.setPlaceClass(PlaceTestData.loadMudPlaceClass(DELETE_DEMISED_PLACE_CLASS));
		expectedDemisedPlace.setAttrs(new HashSet<>());
		
		service.destroyPlace(PlaceServiceImplTests.DELETE_DEMISED_PLACE_ID);
		
		verify(placeRepository).save(expectedDemisedPlace);
	}
	
	@Test
	public void testDelete() {
		
		service.destroyPlace(PlaceTestData.READ_PLACE_ID);
		
		verify(placeRepository).deleteById(PlaceTestData.READ_PLACE_ID);
	}
	
	
	private void checkAttrMap(Place changedPlace, String expectedClassName) throws IOException {
		
		MudPlaceClass changedPlaceClass = PlaceTestData.loadMudPlaceClass(expectedClassName);
		
		// Checking if all attributes exist in updated place
		assertThat(changedPlaceClass.getAttrs().stream()
				.allMatch(curClassAttr -> 
					changedPlace.getAttrs().containsKey(curClassAttr.getCode()) &&
					changedPlace.getAttrs().get(curClassAttr.getCode()).equals(curClassAttr.getValue())
				)
				).isTrue();
	}
	
}
