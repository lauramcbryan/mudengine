package com.jpinfo.mudengine.client.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.message.Message;

@Component
public class MudClientScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(MudClientScheduler.class);
	
	private static final Long WARN_IDLE_TIMEOUT = 240000L;
	
	private static final Long KILL_IDLE_TIMEOUT = 300000L;
	
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private MudengineApi api;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
	
	@Scheduled(fixedDelay=1000)
	public void updateClientScreen() {

		// Check all connected clients that didn't received the greetings message
		saluteNewConnections();
		
		// Check messages for all connected clients that has being selected
		checkNotifications();
		
		// Check idle connections
		checkInactiveConnections();

	}
	
	private void checkNotifications() {
		
		gateway.getActiveConnections().values().stream()
			.filter(ClientConnection::hasBeingSelected)
			.forEach(d -> {
			
				// Check the messages of this being					
				try {
					List<Message> messageList= api.getMessages(d.getAuthToken());
					
					// Sending all the messages received
					for(Message curMessage: messageList) {
	
						// Send the message over tcp
						d.sendMessage(curMessage);
					}
					
					
				} catch (ClientException e) {
					log.error("Error while updating messages: ", e);
				}
		});
		
	}
	
	private void saluteNewConnections() {

		gateway.getActiveConnections().values().stream()
			.filter(ClientConnection::isNeedGreetings)
			.forEach(d -> {
			
				// Check the messages of this being					
				try {
					d.sendFile(ClientHelper.GREETINGS_FILE);
					d.setNeedGreetings(false);
					
				} catch (Exception e) {
					log.error("Error while sending greetings ", e);
				}
		});
	}
	
	private void checkInactiveConnections() {

		// Check all connected clients inactive for longer that the warn idle timeout
		gateway.getActiveConnections().values().stream()
			.filter(d -> (System.currentTimeMillis() - d.getLastActivity() > MudClientScheduler.WARN_IDLE_TIMEOUT)
					&& !d.isInactivityWarning())
			.forEach(d -> {
				d.sendMessage("");
				d.sendMessage(LocalizedMessages.WARN_IDLE_TIMEOUT);
				d.setInactivityWarning(true);
			});
		
		// Check all inactive clients
		gateway.getActiveConnections().values().stream()
			.filter(d -> (System.currentTimeMillis() - d.getLastActivity() > MudClientScheduler.KILL_IDLE_TIMEOUT))
			.forEach(d -> {
				d.sendMessage("");
				d.sendMessage(LocalizedMessages.KILL_IDLE_TIMEOUT);
				connFactory.closeConnection(d.getConnection().getConnectionId());
			});
	}
}
