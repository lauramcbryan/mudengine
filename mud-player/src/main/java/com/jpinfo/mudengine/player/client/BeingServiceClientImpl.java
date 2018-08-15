package com.jpinfo.mudengine.player.client;


import java.util.HashMap;


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
	public Being createPlayerBeing(Long playerId, String beingClass, String worldName,
			Integer placeCode, String beingName) {
		
		Being result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		
		urlVariables.put("playerId", playerId);
		urlVariables.put("beingClass", beingClass);
		urlVariables.put("worldName", worldName);
		urlVariables.put("placeCode", placeCode);
		urlVariables.put("beingName", beingName);
		
		try {
			ResponseEntity<Being> responseService= restTemplate.exchange(
					"/being/player/{playerId}?beingClass={beingClass}&worldName={worldName}&placeCode={placeCode}&beingName={beingName}", 
					HttpMethod.PUT, getEmptyHttpEntity(), Being.class, urlVariables);
			
			if (responseService.getStatusCode()==HttpStatus.CREATED) {
				result = responseService.getBody();
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}

	@Override
	public void destroyBeing(Long beingCode) {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("beingCode", beingCode);
	
		try {
			restTemplate.exchange(beingEndpoint + "/being/{beingCode}", 
					HttpMethod.DELETE, getEmptyHttpEntity(), Void.class, urlVariables);
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
	}

}
