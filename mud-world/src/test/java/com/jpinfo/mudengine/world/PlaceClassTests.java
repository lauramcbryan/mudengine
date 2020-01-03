package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.service.PlaceClassServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceClassTests {

	@MockBean
	private PlaceClassRepository mockClassRepository;
	
	@MockBean
	private TokenService tokenUtils;
	
	@Autowired
	private PlaceClassServiceImpl service;
	
	
	@PostConstruct
	public void setup() throws IOException {
		
		given(mockClassRepository.findById(ArgumentMatchers.anyString()))
			.willAnswer(i -> {
				
				return Optional.of(
						PlaceTestData.loadMudPlaceClass(i.getArgument(0, String.class))
						);
			});
	}
	
	@Test
	public void testGetPlaceClass() throws IOException {
		
		MudPlaceClass dbClass = PlaceTestData.loadMudPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
		PlaceClass responseClass = 
				service.getPlaceClass(PlaceTestData.UPDATE_CLASS_PLACE_CLASS);
		
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
