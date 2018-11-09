package com.jpinfo.mudengine.being.client;

import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;

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
}
