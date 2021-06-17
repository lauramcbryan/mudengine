package com.jpinfo.mudengine.world.service.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.world.PlaceTestData;
import com.jpinfo.mudengine.world.model.MudPlaceClass;

@ExtendWith(MockitoExtension.class)
public class PlaceClassConverterTests {
	
	@Test
	void test() throws IOException {
		
		MudPlaceClass dbClass = PlaceTestData.loadMudPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
		PlaceClass responseClass = PlaceClassConverter.convert(dbClass);
		
		// Check the fields
		assertThat(responseClass.getPlaceClassCode()).isEqualTo(dbClass.getCode());
		
		assertThat(responseClass.getDescription()).isEqualTo(dbClass.getDescription());
		assertThat(responseClass.getName()).isEqualTo(dbClass.getName());
		assertThat(responseClass.getSizeCapacity()).isEqualTo(dbClass.getSizeCapacity());
		assertThat(responseClass.getWeightCapacity()).isEqualTo(dbClass.getWeightCapacity());
		assertThat(responseClass.getBuildCost()).isEqualTo(dbClass.getBuildCost());
		assertThat(responseClass.getBuildEffort()).isEqualTo(dbClass.getBuildEffort());
		assertThat(responseClass.getDemisePlaceClassCode()).isEqualTo(dbClass.getDemisedPlaceClassCode());
		assertThat(responseClass.getParentClassCode()).isEqualTo(dbClass.getParentClassCode());
		
		assertThat(dbClass.getAttrs().stream()
				.allMatch(curAttr ->
					responseClass.getAttrs().containsKey(curAttr.getCode())
					&&
					responseClass.getAttrs().get(curAttr.getCode()).equals(curAttr.getValue())
				)
				).isTrue();
	}

}
