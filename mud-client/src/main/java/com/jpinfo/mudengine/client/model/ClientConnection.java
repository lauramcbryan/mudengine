package com.jpinfo.mudengine.client.model;

import java.util.Optional;


import org.springframework.integration.ip.tcp.connection.TcpConnection;

import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;

public class ClientConnection {

	private Optional<Player> playerData;
	
	private String authToken;
	
	private Being activeBeing;
	
	private Place curPlace;
	
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
		this.playerData = Optional.ofNullable(playerData);
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
		
		return this.activeBeing!=null;
	}
	
	public Optional<Long> getActiveBeingCode() {
		
		if (this.activeBeing!=null) {
			return Optional.of(this.activeBeing.getBeingCode());
		} else {
			return Optional.empty();
		}
	}

	public Being getActiveBeing() {
		return activeBeing;
	}

	public void setActiveBeing(Being activeBeing) {
		this.activeBeing = activeBeing;
	}

	public Place getCurPlace() {
		return curPlace;
	}

	public void setCurPlace(Place curPlace) {
		this.curPlace = curPlace;
	}
}
