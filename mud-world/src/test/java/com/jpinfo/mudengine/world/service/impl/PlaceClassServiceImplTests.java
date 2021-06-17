package com.jpinfo.mudengine.world.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.world.PlaceTestData;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;

@ExtendWith(MockitoExtension.class)
public class PlaceClassServiceImplTests {

	@Mock
	private PlaceClassRepository mockClassRepository;
	
	@InjectMocks
	private PlaceClassServiceImpl service;
	
	@Test
	public void testGetPlaceClass() throws IOException {
		
		MudPlaceClass dbClass = PlaceTestData.loadMudPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
		given(mockClassRepository.findById(PlaceTestData.UPDATE_CLASS_PLACE_CLASS))
		.willAnswer(i -> {
			return Optional.of(dbClass);
		});
		
		PlaceClass responseClass = 
				service.getPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
		// Check the fields
		assertThat(responseClass.getPlaceClassCode()).isEqualTo(dbClass.getCode());
	}
}
