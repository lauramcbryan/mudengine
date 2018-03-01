package com.jpinfo.mudengine.client.model;

import org.springframework.integration.ip.tcp.connection.TcpConnection;

public class ClientConnection {

	private Long playerId;
	
	private Long beingId;
	
	private String authToken;
	
	private TcpConnection connection;
	
	
	public ClientConnection(TcpConnection connection) {
		this.connection = connection;
	}


	public Long getPlayerId() {
		return playerId;
	}


	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}


	public Long getBeingId() {
		return beingId;
	}


	public void setBeingId(Long beingId) {
		this.beingId = beingId;
	}


	public String getAuthToken() {
		return authToken;
	}


	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}


	public TcpConnection getConnection() {
		return connection;
	}
}
