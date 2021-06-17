package com.jpinfo.mudengine.world.service.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.world.PlaceTestData;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;

@ExtendWith(MockitoExtension.class)
public class PlaceConverterTests {

	@Test
	void test() throws IOException {
		
		MudPlace dbPlace = PlaceTestData.loadMudPlace(PlaceTestData.READ_PLACE_ID);
				
		Place responsePlace = PlaceConverter.convert(dbPlace);
		
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
