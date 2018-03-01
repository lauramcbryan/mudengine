package com.jpinfo.mudengine.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.model.ClientConnection;

@MessageEndpoint
public class MudClientService {
	
	@Autowired
	private MudClientGateway gateway;
	
	@ServiceActivator(inputChannel="plainRequestChannel")
	public void handleCommand(@Header(name="ip_connectionId") String connectionId, String in) {
		
		// handle the command
		ClientConnection client = gateway.getActiveConnections().get(connectionId);
		
		// Check if the client is registered.  If it's not, show the welcome screen
		
		if (client.getAuthToken()!=null) {
			
		}
		
	}

}
