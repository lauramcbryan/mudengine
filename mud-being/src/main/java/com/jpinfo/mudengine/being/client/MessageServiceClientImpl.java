package com.jpinfo.mudengine.being.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class MessageServiceClientImpl extends BaseServiceClient implements MessageServiceClient {

	@Value("${message.endpoint}")
	private String messageEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();

	
	@Override
	public void putMessage(Long targetCode, String message, String... parms) {
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("targetCode", targetCode);
		urlVariables.put("message", message);
		
		if (parms!=null)
			urlVariables.put("parms", String.join(", ", parms));

	
		try {
			restTemplate.exchange(messageEndpoint + 
					"/message/being/{targetCode}?message={message}&parms={parms}", HttpMethod.PUT, 
					getEmptyHttpEntity(), Void.class, urlVariables);

		} catch(RestClientResponseException e) {
			handleError(e);
		}
	}

}
