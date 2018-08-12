package com.jpinfo.mudengine.player.client;


import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.GeneralException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.utils.ApiErrorMessage;
import com.jpinfo.mudengine.common.utils.CommonConstants;

@Component
public class BeingServiceClientImpl implements BeingServiceClient {
	
	
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
	
	private HttpEntity<Object> getEmptyHttpEntity() {
		
		String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, authToken);
		
		return new HttpEntity<>(authHeaders);
	}
	
	
	private void handleError(RestClientResponseException exception) {

		try {
			ApiErrorMessage restError = ApiErrorMessage.build(exception.getResponseBodyAsString());
			
			switch(restError.getStatus()) {
			
				case 400:
					throw new IllegalParameterException(restError.getMessage());
				case 403:
					throw new AccessDeniedException(restError.getMessage());
				case 404:
					throw new EntityNotFoundException(restError.getMessage());
				default:
					throw new GeneralException("api.error.message");
			}
		} catch(Exception e) {
			throw new GeneralException("api.error.message");
		}
	}


}
