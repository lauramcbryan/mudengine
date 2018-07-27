package com.jpinfo.mudengine.client;

import java.util.HashMap;

import java.util.Map;

import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.model.ClientConnection;

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
	}

	@Override
	public void removeDeadConnection(TcpConnection connection) {

		this.activeConnections.remove(connection.getConnectionId());
		
		super.removeDeadConnection(connection);
	}

	@Override
	protected void doStart() {
		
		this.activeConnections = new HashMap<>();
		
		super.doStart();
	}

	public Map<String, ClientConnection> getActiveConnections() {
		return activeConnections;
	}
	
	

}
