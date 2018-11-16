package com.jpinfo.mudengine.action.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class MessageServiceClientImpl extends BaseServiceClient implements MessageServiceClient {

	@Value("${message.endpoint}")
	private String messageEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();

	
	@Override
	public void putMessage(Long targetCode, MessageRequest request) {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("targetCode", targetCode);
		
		HttpEntity<MessageRequest> requestEntity = new HttpEntity<>(request, getAuthHeaders()); 
	
		try {
			restTemplate.exchange(messageEndpoint + 
					"/message/being/{targetCode}", 
					HttpMethod.PUT, requestEntity, Long.class, urlVariables);

		} catch(RestClientResponseException e) {
			handleError(e);
		}
	}
	
	@Override
	public void broadcastMessage(Integer placeCode, MessageRequest request) {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("placeCode", placeCode);
		
		HttpEntity<MessageRequest> requestEntity = new HttpEntity<>(request, getAuthHeaders());
	
		try {
			restTemplate.exchange(messageEndpoint + 
					"/message/place/{placeCode}", 
					HttpMethod.PUT, requestEntity, Void.class, urlVariables);

		} catch(RestClientResponseException e) {
			handleError(e);
		}
	}

}
