package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.place.Place;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MudWorldApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	

	@Test
	public void getPlaceTest() {
		
		Integer placeId = 1;
		
		ResponseEntity<Place> response = restTemplate.getForEntity("/place/" + placeId, Place.class);
		
		assertThat(response.getStatusCode().is2xxSuccessful());
		assertThat(response.getBody().getPlaceCode()).isEqualTo(placeId);
	}
	
	@Test
	public void createPlaceTest() {
		
		String placeClass="TUNNEL";
		String direction = "UP";
		Integer targetPlaceId = 1;
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("placeClassCode", placeClass);
		urlVariables.put("direction", direction);
		urlVariables.put("targetPlaceCode", targetPlaceId);
			
		ResponseEntity<Place> response = restTemplate.exchange(
				"/place/?placeClassCode={placeClassCode}&direction={direction}&targetPlaceCode={targetPlaceCode}", 
				HttpMethod.PUT, null, Place.class, urlVariables);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		
		
	}

}
