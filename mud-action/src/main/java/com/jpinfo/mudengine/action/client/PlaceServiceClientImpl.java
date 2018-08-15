package com.jpinfo.mudengine.action.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class PlaceServiceClientImpl extends BaseServiceClient implements PlaceServiceClient {

	@Value("${place.endpoint}")
	private String placeEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	
	@Override
	public Place getPlace(Integer placeId) {
		
		Place response  = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("placeId", placeId);
	
		try {
			ResponseEntity<Place> serviceResponse = restTemplate.exchange(placeEndpoint + "/place/{placeId}", 
					HttpMethod.GET, getEmptyHttpEntity(), Place.class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = serviceResponse.getBody();
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
	}

	@Override
	public Place updatePlace(Integer placeId, Place requestPlace) {
		// TODO Auto-generated method stub
		return null;
	}

}
