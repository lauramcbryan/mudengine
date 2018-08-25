package com.jpinfo.mudengine.action.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;
import com.jpinfo.mudengine.common.utils.CommonConstants;

@Component
public class BeingServiceClientImpl extends BaseServiceClient implements BeingServiceClient {

	@Value("${being.endpoint}")
	private String beingEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();
	

	@Override
	public Being getBeing(Long beingCode) {
		
		Being result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("beingCode", beingCode);
	
		try {
			ResponseEntity<Being> response = restTemplate.exchange(beingEndpoint + "/being/{beingCode}", 
					HttpMethod.GET, getEmptyHttpEntity(), Being.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}

	@Override
	public Being updateBeing(Long beingCode, Being requestBeing) {

		Being result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("beingCode", beingCode);
		
		String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, authToken);
		
		HttpEntity<Being> requestEntity = new HttpEntity<>(requestBeing, authHeaders);
	
		try {
			ResponseEntity<Being> response = restTemplate.exchange(beingEndpoint + "/being/{beingCode}", 
					HttpMethod.POST, requestEntity, Being.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}
	
	public List<Being> getAllFromPlace(String worldName, Integer placeCode) {
		
		List<Being> result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("worldName", worldName);
		urlVariables.put("placeCode", placeCode);
	
		try {
			ResponseEntity<Being[]> response = restTemplate.exchange(beingEndpoint + "/being/place/{worldName}/{placeCode}", 
					HttpMethod.GET, getEmptyHttpEntity(), Being[].class, urlVariables);
			
			if (response.getStatusCode().equals(HttpStatus.OK))
				result = Arrays.asList(response.getBody());
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}

}
