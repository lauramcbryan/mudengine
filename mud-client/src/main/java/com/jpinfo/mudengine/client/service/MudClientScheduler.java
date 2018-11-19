package com.jpinfo.mudengine.client.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.jpinfo.mudengine.client.utils.ClientLocalizedMessages;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.message.MessageEntity;
import com.jpinfo.mudengine.common.place.Place;

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
			
				try {

					// Check the messages of this being					
					List<Message> messageList= api.getMessages(d.getAuthToken());
					
					// Gather the entity list to be changed
					// We have it grouped to avoid having to update the same entity twice
					Set<MessageEntity> changedEntitySet = new HashSet<>();
					
					// Sending all the messages received
					for(Message curMessage: messageList) {
	
						// Send the message over tcp
						d.sendMessage(curMessage);
						
						// In this message we can have changed entities 
						// that need to be updated.
						if (curMessage.getChangedEntities()!=null) {
							
							changedEntitySet.addAll(curMessage.getChangedEntities());
						}
					}
					
					// Checking and updating the changed entities
					checkChangedEntities(changedEntitySet, d);
					
				} catch (ClientException e) {
					log.error("Error while updating messages: ", e);
				}
		});
		
	}
	
	private void checkChangedEntities(Set<MessageEntity> changedEntitySet, ClientConnection client) throws ClientException {
		
		Being activeBeing = client.getActiveBeing().orElse(new Being());
		Place curPlace = client.getCurPlace().orElse(new Place());

		for(MessageEntity changedEntity: changedEntitySet) {
			
			switch(changedEntity.getEntityType()) {
			case BEING:
				
				// If current active being is changing
				if (changedEntity.getEntityId().equals(activeBeing.getCode())) {
					
					// Update current being
					client.setActiveBeing(api.getBeing(client.getAuthToken(), changedEntity.getEntityId()));
				}
				
				break;
			case ITEM:
				
				// Check if the changed item is in active being possession
				if (activeBeing.getEquipment().values().stream()
					.anyMatch(equippedItem -> equippedItem.getCode().equals(changedEntity.getEntityId()))) {

					// Update current being (the equipment will be updated along)
					client.setActiveBeing(api.getBeing(client.getAuthToken(), changedEntity.getEntityId()));
				}
				
				break;
			case PLACE:
				
				if (changedEntity.getEntityId().equals(curPlace.getCode().longValue())) {
					
					// Update current place
					client.setCurPlace(api.getPlace(client.getAuthToken(), changedEntity.getEntityId().intValue()));
				}
				
				break;
				
			default:
				break;
			}
		}
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
				d.sendMessage(ClientLocalizedMessages.WARN_IDLE_TIMEOUT);
				d.setInactivityWarning(true);
			});
		
		// Check all inactive clients
		gateway.getActiveConnections().values().stream()
			.filter(d -> (System.currentTimeMillis() - d.getLastActivity() > MudClientScheduler.KILL_IDLE_TIMEOUT))
			.forEach(d -> {
				d.sendMessage("");
				d.sendMessage(ClientLocalizedMessages.KILL_IDLE_TIMEOUT);
				connFactory.closeConnection(d.getConnection().getConnectionId());
			});
	}
}
