package com.jpinfo.mudengine.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

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
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.world.client.BeingServiceClient;
import com.jpinfo.mudengine.world.client.ItemServiceClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class PlaceTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private ItemServiceClient mockItem;
	
	@MockBean
	private BeingServiceClient mockBeing;
	
	private static final String direction = "UP";
	private static final Integer targetPlaceId = 1;
	
	private static final String testPlaceClass="TEST";
	private static final String testPlaceAttr="HP";
	private static final Integer testPlaceAttrValue=50;
	private static final String testPlaceAttr2="MAXHP";
	private static final Integer testPlaceAttrValue2=500;
	
	private static final String changedPlaceClass="TESTBLDG";
	private static final String changedPlaceAttr="HP2";
	private static final Integer changedPlaceAttrValue=3;
	private static final String changedPlaceAttr2="MAXH2";
	private static final Integer changedPlaceAttrValue2=8;
	
	private static final String extraPlaceAttr="HP3";
	
	@Test
	public void contextLoads() {
		
	}
	
	@Test
	public void testCrudPlace() {
		
		// Creating the authentication token
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(TokenService.HEADER_TOKEN, TokenService.buildInternalToken());
		
		HttpEntity<Object> authEntity = new HttpEntity<Object>(authHeaders);
		
		// *********** CREATE **********
		// =============================
		
		Map<String, Object>  urlVariables = new HashMap<String, Object>();
		
		urlVariables.put("placeClassCode", PlaceTests.testPlaceClass);
		urlVariables.put("direction", PlaceTests.direction);
		urlVariables.put("targetPlaceCode", PlaceTests.targetPlaceId);
			
		ResponseEntity<Place> responseCreate= restTemplate.exchange(
				"/place/?placeClassCode={placeClassCode}&direction={direction}&targetPlaceCode={targetPlaceCode}", 
				HttpMethod.PUT, authEntity, Place.class, urlVariables);
		
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertValues(responseCreate, PlaceTests.testPlaceClass);
		assertTestPlaceClassValues(responseCreate.getBody());
		
		urlVariables.put("placeId", responseCreate.getBody().getPlaceCode());

		// ************ READ ***********
		// =============================		
		
		ResponseEntity<Place> responseGet = restTemplate.exchange("/place/{placeId}", 
				HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseGet.getStatusCode().is2xxSuccessful());
		assertValues(responseGet, PlaceTests.testPlaceClass);
		assertTestPlaceClassValues(responseGet.getBody());
		
		Place dummyPlace = responseGet.getBody();

		// ***** UpdatePlace *****
		
		dummyPlace.setPlaceClassCode(PlaceTests.changedPlaceClass);
		dummyPlace.getAttrs().put(PlaceTests.extraPlaceAttr, new Integer(1));
		
		HttpEntity<Place> request = new HttpEntity<Place>(dummyPlace, authHeaders);
			
		ResponseEntity<Place> responseUpdate = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.POST, request, Place.class, urlVariables);
		
		assertThat(responseUpdate.getStatusCode().is2xxSuccessful());
		assertValues(responseUpdate, PlaceTests.changedPlaceClass);
		assertThat(responseUpdate.getBody().getAttrs().get(PlaceTests.extraPlaceAttr)).isNotNull();
		assertChangedPlaceClassValues(responseUpdate.getBody());

		// ***** deletePlace *****
		
		ResponseEntity<Place> responseFirstDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.DELETE, authEntity, Place.class, urlVariables);
		
		assertThat(responseFirstDelete.getStatusCode().is2xxSuccessful());
		
		// Querying the deleted object
		ResponseEntity<Place> responseAfterFirstDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		// Must revert to first placeClass (changedPlaceClass demise placeClass)
		assertThat(responseAfterFirstDelete.getStatusCode().is2xxSuccessful());
		assertValues(responseAfterFirstDelete, PlaceTests.testPlaceClass);
		assertTestPlaceClassValues(responseAfterFirstDelete.getBody());
		
		// Deleting it again must remove the entity
		ResponseEntity<Place> responseSecondDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.DELETE, authEntity, Place.class, urlVariables);
		
		assertThat(responseSecondDelete.getStatusCode().is2xxSuccessful());
		
		// Querying again
		ResponseEntity<Place> responseAfterSecondDelete = restTemplate.exchange(
				"/place/{placeId}", HttpMethod.GET, authEntity, Place.class, urlVariables);
		
		assertThat(responseAfterSecondDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
	}
	
	private void assertValues(ResponseEntity<Place> response, String placeClass) {
		
		Place responsePlace = response.getBody();
		
		assertThat(responsePlace).isNotNull();
		assertThat(responsePlace.getPlaceClassCode()).isEqualTo(placeClass);
		assertThat(responsePlace.getExits().get(PlaceTests.direction)).isNotNull();
		
		PlaceExit responseExitPlace = responsePlace.getExits().get(PlaceTests.direction);
		
		assertThat(responseExitPlace.getTargetPlaceCode()).isEqualTo(PlaceTests.targetPlaceId);
	}
	
	private void assertTestPlaceClassValues(Place place) {
		
		assertThat(place.getAttrs().get(PlaceTests.testPlaceAttr)).isNotNull();
		assertThat(place.getAttrs().get(PlaceTests.testPlaceAttr)).isEqualTo(PlaceTests.testPlaceAttrValue);
		
		assertThat(place.getAttrs().get(PlaceTests.testPlaceAttr2)).isNotNull();
		assertThat(place.getAttrs().get(PlaceTests.testPlaceAttr2)).isEqualTo(PlaceTests.testPlaceAttrValue2);
	}
	
	private void assertChangedPlaceClassValues(Place place) {
		
		assertThat(place.getAttrs().get(PlaceTests.changedPlaceAttr)).isNotNull();
		assertThat(place.getAttrs().get(PlaceTests.changedPlaceAttr)).isEqualTo(PlaceTests.changedPlaceAttrValue);
		
		assertThat(place.getAttrs().get(PlaceTests.changedPlaceAttr2)).isNotNull();
		assertThat(place.getAttrs().get(PlaceTests.changedPlaceAttr2)).isEqualTo(PlaceTests.changedPlaceAttrValue2);
	}
}
