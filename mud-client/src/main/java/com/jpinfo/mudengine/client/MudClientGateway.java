package com.jpinfo.mudengine.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.utils.ClientHelper;

@Component
public class MudClientGateway extends TcpInboundGateway {
	
	private Map<String, ClientConnection> activeConnections;

	@Override
	public void addNewConnection(TcpConnection connection) {

		// Creating a wrapper for the TcpConnection
		// Inside this wrapper we'll keep the player state data
		ClientConnection c = new ClientConnection(connection);
		
		this.activeConnections.put(connection.getConnectionId(), c);
		
		
		// call the super method 
		super.addNewConnection(connection);
		
		
		// Send the greetings message
		try {
			ClientHelper.sendMessage(c, ClientHelper.GREETINGS);
		} catch(Exception e) {
			// Disregard this exception
		}
	}

	@Override
	public void removeDeadConnection(TcpConnection connection) {

		// Send the goodbye message
		try {
			ClientHelper.sendMessage(connection, ClientHelper.GOODBYE);
		} catch(Exception e) {
			// Disregard this exception
		}

		this.activeConnections.remove(connection.getConnectionId());
		
		super.removeDeadConnection(connection);
	}

	@Override
	protected void doStart() {
		
		this.activeConnections = new HashMap<String, ClientConnection>();
		
		super.doStart();
	}

	public Map<String, ClientConnection> getActiveConnections() {
		return activeConnections;
	}
	
	

}
