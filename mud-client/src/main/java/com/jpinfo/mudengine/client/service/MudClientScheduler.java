package com.jpinfo.mudengine.client.service;


import java.util.HashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.message.Message;

@Component
public class MudClientScheduler {
	
	@Autowired
	private MudClientGateway gateway;
	
	private RestTemplate restTemplate;
	
	@Scheduled(fixedDelay=1000)
	public void updateClientScreen() {
		
		// For each client:
		// 		Check if there's pending messages and write them in client terminal
		gateway.getActiveConnections().values()
			.stream()
			.forEach((t) -> {
				
				// Player logged and with a being attached
				if (t.isLogged() && t.hasBeingSelected()) {
					
					// Check the messages of this being
					HttpHeaders clientHeaders = new HttpHeaders();
					clientHeaders.add(ClientHelper.HEADER_TOKEN, t.getAuthToken());
					
					HttpEntity<Object> authEntity = new HttpEntity<Object>(clientHeaders);
	
					ResponseEntity<Message[]> responseRead = restTemplate.exchange(
							"/message", HttpMethod.GET, authEntity, Message[].class, new HashMap<String, Object>());
					
					if (responseRead.getStatusCode().is2xxSuccessful()) {
	
						// Sending all the messages received
						for(Message curMessage: responseRead.getBody()) {
	
							try {
	
								// Send the message over tcp
								ClientHelper.sendMessage(t, curMessage);
	
							} catch (Exception e) {
								
								e.printStackTrace();
							}
						}
					} // end if responseRead = SUCCESSFUL
				} // end if being !=null
				else 
					// Player not logged.  Just check if I already gave greetings.
					if (!t.isLogged()) {

						if (t.isNeedGreetings()) {
							
							try {
								ClientHelper.sendFile(t, ClientHelper.GREETINGS_FILE);
								t.setNeedGreetings(false);
								
								
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
			
		});
	}
}
