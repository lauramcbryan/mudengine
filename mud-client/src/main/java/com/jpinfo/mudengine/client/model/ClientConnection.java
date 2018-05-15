package com.jpinfo.mudengine.client.model;

import org.springframework.integration.ip.tcp.connection.TcpConnection;

public class ClientConnection {

	private String username;
	
	private Long currentBeingId;
	
	private String authToken;
	
	private boolean needGreetings;
	
	private TcpConnection connection;
	
	private Command curCommand;
	
	private CommandParam curParam;
	
	
	public ClientConnection(TcpConnection connection) {
		this.connection = connection;
		this.needGreetings = true;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}



	public Long getCurrentBeingId() {
		return currentBeingId;
	}



	public void setCurrentBeingId(Long currentBeingId) {
		this.currentBeingId = currentBeingId;
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


	public boolean isNeedGreetings() {
		return needGreetings;
	}


	public void setNeedGreetings(boolean needGreetings) {
		this.needGreetings = needGreetings;
	}


	public Command getCurCommand() {
		return curCommand;
	}


	public void setCurCommand(Command curCommand) {
		this.curCommand = curCommand;
		this.curParam = null;
	}


	public CommandParam getCurParam() {
		return curParam;
	}


	public void setCurParam(CommandParam curParam) {
		this.curParam = curParam;
	}
}
