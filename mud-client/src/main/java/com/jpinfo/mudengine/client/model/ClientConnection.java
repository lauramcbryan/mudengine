package com.jpinfo.mudengine.client.model;

import java.util.Optional;

import org.springframework.integration.ip.tcp.connection.TcpConnection;

import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

public class ClientConnection {

	private Optional<Player> playerData;

	private Optional<Session> playerSession;
	
	private String authToken;
	
	private boolean needGreetings;
	
	private TcpConnection connection;
	
	private CommandState curCommandState;
	
	private CommandParamState curParamState;
	
	
	public ClientConnection(TcpConnection connection) {
		this.connection = connection;
		this.needGreetings = true;
	}


	public String getAuthToken() {
		return authToken;
	}


	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	

	public Optional<Player> getPlayerData() {
		return playerData;
	}


	public void setPlayerData(Player playerData) {
		this.playerData = Optional.of(playerData);
	}


	public Optional<Session> getPlayerSession() {
		return playerSession;
	}


	public void setPlayerSession(Session playerSession) {
		this.playerSession = Optional.of(playerSession);
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


	public CommandState getCurCommandState() {
		return curCommandState;
	}
	
	public Command getCurCommand() {
		
		return (this.curCommandState!=null ? this.curCommandState.getCommand(): null);
		
	}

	public void setCurCommandState(CommandState curCommand) {
		this.curCommandState = curCommand;
		this.curParamState = null;
	}


	public CommandParamState getCurParamState() {
		return curParamState;
	}
	
	public CommandParam getCurParam() {
		return (this.curParamState!=null ? this.curParamState.getParameter(): null);
	}


	public void setCurParamState(CommandParamState curParam) {
		this.curParamState = curParam;
	}
	
	public boolean isLogged() {
		return this.playerData!=null;
	}
	
	public boolean hasBeingSelected() {
		
		return (this.playerSession.isPresent() ? this.playerSession.get().getBeingCode()!=null: false);
	}
}
