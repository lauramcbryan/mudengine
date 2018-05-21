package com.jpinfo.mudengine.client.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.message.Message;

@Component
public class MudClientScheduler {
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private MudengineApi api;
	
	@Scheduled(fixedDelay=1000)
	public void updateClientScreen() throws Exception {
		
		// For each client:
		// 		Check if there's pending messages and write them in client terminal
		gateway.getActiveConnections().values()
			.stream()
			.forEach((t) -> {
				
				try {
				
					// Player logged and with a being attached
					if (t.isLogged() && t.hasBeingSelected()) {
	
						// Check the messages of this being					
						List<Message> messageList = api.getMessages(t.getAuthToken());
		
						// Sending all the messages received
						for(Message curMessage: messageList) {
	
							// Send the message over tcp
							ClientHelper.sendMessage(t, curMessage);
						}
					} // end if being !=null
					else { 
						// Player not logged.  Just check if I already gave greetings.
						if (!t.isLogged()) {
	
							if (t.isNeedGreetings()) {
								
								ClientHelper.sendFile(t, ClientHelper.GREETINGS_FILE);
								t.setNeedGreetings(false);
							}
						}
					}
				} catch(Exception e) {
					System.out.println("Error while updating messages: " + e.getMessage());
				}
			
			});
	}
}
