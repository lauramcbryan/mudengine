package com.jpinfo.mudengine.action.rules;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class ActionRuleService extends BaseServiceClient {
	
	@Value("${rule.endpoint}")
	private String ruleEndpoint;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public ActionInfo prereqCheck(String actionClass, ActionInfo original) {
		
		ActionInfo result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("actionClass", actionClass);
		
		HttpEntity<ActionInfo> requestEntity = new HttpEntity<>(original);
	
		try {
			ResponseEntity<ActionInfo> response = restTemplate.exchange(ruleEndpoint + "/prereq/{actionClass}", 
					HttpMethod.POST, requestEntity, ActionInfo.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}

	public ActionInfo applyEffects(String actionClass, ActionInfo original) {
		
		ActionInfo result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("actionClass", actionClass);
		
		HttpEntity<ActionInfo> requestEntity = new HttpEntity<>(original);
	
		try {
			ResponseEntity<ActionInfo> response = restTemplate.exchange(ruleEndpoint + "/effect/{actionClass}", 
					HttpMethod.POST, requestEntity, ActionInfo.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}
	
	public Integer calculateTurns(String actionClass, ActionInfo original) {
		
		Integer result = null;
		
		Map<String, Object> urlVariables = new HashMap<>();
		urlVariables.put("actionClass", actionClass);
		
		HttpEntity<ActionInfo> requestEntity = new HttpEntity<>(original);
	
		try {
			ResponseEntity<Integer> response = restTemplate.exchange(ruleEndpoint + "/nroturns/{actionClass}", 
					HttpMethod.POST, requestEntity, Integer.class, urlVariables);
			
			result = response.getBody();
			
		} catch(RestClientResponseException e) {
			handleError(e);
		}
		
		return result;
	}
}
