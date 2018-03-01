package com.jpinfo.mudengine.client.service;


import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.security.TokenService;

@Component
public class MudClientScheduler {
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private UpdateClientMessages updateClientMessages;
	
	private RestTemplate restTemplate;
	
	@Scheduled(fixedRate=10000)
	public void updateClientScreen() {
		
		// Pass all the clients in sequence
		// Update client screen
		
		// For each client:
		// 		Check if there's pending messages and write them in client terminal
		gateway.getActiveConnections().values().stream().forEach(updateClientMessages);
	}

	@Component
	class UpdateClientMessages implements Consumer<ClientConnection> {

		@Override
		public void accept(ClientConnection t) {
			
			// If the connection has a being assigned to it
			if (t.getBeingId()!=null) {
				
				// Check the messages of this being
				HttpHeaders clientHeaders = new HttpHeaders();
				clientHeaders.add(TokenService.HEADER_TOKEN, t.getAuthToken());
				
				HttpEntity<Object> authEntity = new HttpEntity<Object>(clientHeaders);

				ResponseEntity<Message[]> responseRead = restTemplate.exchange(
						"/message", HttpMethod.GET, authEntity, Message[].class, new HashMap<String, Object>());
				
				if (responseRead.getStatusCode().is2xxSuccessful()) {

					// Sending all the messages received
					for(Message curMessage: responseRead.getBody()) {
						
						org.springframework.messaging.Message<String> clientMessage = 
								MessageBuilder.withPayload(ClientHelper.formatMessage(curMessage))
									.setHeader("headerName", "headerValue")
									.build();
						
						try {
							t.getConnection().send(clientMessage);
						} catch (Exception e) {
							
							e.printStackTrace();
						}
					}
				} // end if responseRead = SUCCESSFUL
			} // end if being !=null

		} // accept()
	}
}
