package com.jpinfo.mudengine.client.model;

import java.util.Locale;
import java.util.Optional;


import org.springframework.integration.ip.tcp.connection.TcpConnection;

import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;

public class ClientConnection {

	private Optional<Player> playerData;
	
	private String authToken;
	
	private Locale locale;
	
	private Optional<Being> activeBeing;
	
	private Optional<Place> curPlace;
	
	private boolean needGreetings;
	
	private TcpConnection connection;
	
	private CommandState curCommandState;
	private CommandParamState curParamState;
	
	private LocalizedMessages messages;
	
	
	public ClientConnection(TcpConnection connection) {
		this.connection = connection;
		this.needGreetings = true;
		this.locale = ClientHelper.DEFAULT_LOCALE;
		this.messages = new LocalizedMessages(this.locale);
		
		this.playerData = Optional.empty();
		this.activeBeing = Optional.empty();
		this.curPlace = Optional.empty();
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
		
		this.locale = Locale.forLanguageTag(playerData.getLocale());
		this.messages = new LocalizedMessages(this.locale);
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
		return this.playerData.isPresent();
	}

	public boolean hasBeingSelected() {
		
		return this.activeBeing.isPresent();
	}
	
	public Optional<Long> getActiveBeingCode() {
		
		if (this.activeBeing.isPresent()) {
			return Optional.of(this.activeBeing.get().getBeingCode());
		} else {
			return Optional.empty();
		}
	}

	public Optional<Being> getActiveBeing() {
		return activeBeing;
	}

	public void setActiveBeing(Optional<Being> activeBeing) {
		this.activeBeing = activeBeing;
	}

	public void setActiveBeing(Being activeBeing) {
		
		if (activeBeing!=null)
			this.activeBeing = Optional.of(activeBeing);
		else
			this.activeBeing = Optional.empty();
	}
	
	public Optional<Place> getCurPlace() {
		return curPlace;
	}

	public void setCurPlace(Optional<Place> curPlace) {
		this.curPlace = curPlace;
	}

	public void setCurPlace(Place curPlace) {
		
		if (curPlace!=null)
			this.curPlace = Optional.of(curPlace);
		else
			this.curPlace = Optional.empty();
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		setLocale(Locale.forLanguageTag(locale));
	}

	public void setLocale(Locale newLocale) {
		
		// If it's changing the locale, reload the messages
		if ((newLocale!=null) && (!newLocale.equals(this.locale))) {
			this.messages = new LocalizedMessages(newLocale);
		}
		
		this.locale = newLocale;
	}
	
	public String getLocalizedMessage(String key) {
		return this.messages.getMessage(key);
	}
	
	public void clearState() {

		clearBeingInformation();
		
		this.authToken = null;
		this.playerData = Optional.empty();
	}
	
	public void clearBeingInformation() {
		this.activeBeing = Optional.empty();
		this.curPlace = Optional.empty();
	}
}
