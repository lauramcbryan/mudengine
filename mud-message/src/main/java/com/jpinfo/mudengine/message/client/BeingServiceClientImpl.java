package com.jpinfo.mudengine.message.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class BeingServiceClientImpl extends BaseServiceClient implements BeingServiceClient {

	@Value("${being.endpoint}")
	private String beingEndpoint;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@Override
	public List<Being> getAllFromPlace(String worldName, Integer placeCode) {

		List<Being> response = new ArrayList<>();
		
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("worldName", worldName);
		urlVariables.put("placeCode", placeCode);
	
		try {
			ResponseEntity<Being[]> serviceResponse = 
					restTemplate.exchange(beingEndpoint + "/item/place/{worldName}/{placeCode}", 
					HttpMethod.DELETE, getEmptyHttpEntity(), Being[].class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK))
				response = Arrays.asList(serviceResponse.getBody());
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
	}

}
