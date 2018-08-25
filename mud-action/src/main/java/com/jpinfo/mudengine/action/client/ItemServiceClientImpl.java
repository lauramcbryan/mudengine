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

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;
import com.jpinfo.mudengine.common.utils.CommonConstants;

@Component
public class ItemServiceClientImpl extends BaseServiceClient implements ItemServiceClient {

	@Value("${item.endpoint}")
	private String itemEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public Item getItem(Long itemId) {
		
		Item response  = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemCode", itemId);
	
		try {
			ResponseEntity<Item> serviceResponse = restTemplate.exchange(itemEndpoint + "/item/{itemCode}", 
					HttpMethod.GET, getEmptyHttpEntity(), Item.class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = serviceResponse.getBody();
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
	}

	@Override
	public Item updateItem(Long itemId, Item item) {
		
		Item response  = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("itemCode", itemId);
		
		String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, authToken);
		
		HttpEntity<Item> requestEntity = new HttpEntity<>(item, authHeaders);
		
	
		try {
			ResponseEntity<Item> serviceResponse = restTemplate.exchange(itemEndpoint + "/item/{itemCode}", 
					HttpMethod.POST, requestEntity, Item.class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = serviceResponse.getBody();
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
	}
	
	@Override
	public List<Item> getAllFromBeing(Long owner) {
		
		List<Item> response  = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("owner", owner);
	
		try {
			ResponseEntity<Item[]> serviceResponse = restTemplate.exchange(itemEndpoint + "/item/being/{owner}", 
					HttpMethod.GET, getEmptyHttpEntity(), Item[].class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = Arrays.asList(serviceResponse.getBody());
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
		
	}
	
	public List<Item> getAllFromPlace(String worldName, Integer placeCode) {
		
		List<Item> response  = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("worldName", worldName);
		urlVariables.put("placeCode", placeCode);
	
		try {
			ResponseEntity<Item[]> serviceResponse = restTemplate.exchange(itemEndpoint + "/item/place/{worldName}/{placeCode}", 
					HttpMethod.GET, getEmptyHttpEntity(), Item[].class, urlVariables);
			
			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = Arrays.asList(serviceResponse.getBody());
			}
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return response;
	}

}
