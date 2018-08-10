package com.jpinfo.mudengine.client.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.messaging.support.MessageBuilder;

import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ClientConnection {
	
	private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
	
	private static final byte[] ECHO_OFF_SEQUENCE = {(byte)0xff, (byte)0xfb, (byte)0x01, (byte)0x00};
	private static final byte[] ECHO_ON_SEQUENCE =  {(byte)0xff, (byte)0xfc, (byte)0x01, (byte)0x00};

	private Optional<Player> playerData;
	
	private String authToken;
	
	private Optional<Being> activeBeing;
	
	private Optional<Place> curPlace;
	
	private boolean needGreetings;
	
	@Setter(AccessLevel.NONE)
	private TcpConnection connection;
	
	private CommandState curCommandState;
	private CommandParamState curParamState;
	
	@Setter(AccessLevel.NONE)
	private LocalizedMessages messages;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean echoEnabled;
	
	private boolean admin;
	
	private Long lastActivity;
	
	private boolean inactivityWarning;
	
	
	public ClientConnection(TcpConnection connection) {
		this.connection = connection;
		this.needGreetings = true;
		this.messages = new LocalizedMessages(ClientHelper.DEFAULT_LOCALE);
		
		this.playerData = Optional.empty();
		this.activeBeing = Optional.empty();
		this.curPlace = Optional.empty();
		this.echoEnabled = true;
		this.lastActivity = System.currentTimeMillis();
	}
	
	public void setLastActivity(Long value) {
		this.lastActivity = value;
		this.setInactivityWarning(false);
	}
	
	
	public void setPlayerData(Player playerData) {
		this.playerData = Optional.ofNullable(playerData);
		
		this.playerData.ifPresent(d -> 
			this.setLocale(d.getLocale())
		);
	}

	public Command getCurCommand() {
		return (this.curCommandState!=null ? this.curCommandState.getCommand(): null);
	}

	public void setCurCommandState(CommandState curCommand) {
		this.curCommandState = curCommand;
		this.curParamState = null;
	}
	
	public CommandParam getCurParam() {
		return (this.curParamState!=null ? this.curParamState.getParameter(): null);
	}
	
	public boolean isLogged() {
		return this.playerData.isPresent();
	}

	public boolean hasBeingSelected() {
		
		return this.activeBeing.isPresent();
	}
	
	public Optional<Long> getActiveBeingCode() {
		
		if (this.activeBeing.isPresent()) {
			return Optional.of(this.activeBeing.get().getCode());
		} else {
			return Optional.empty();
		}
	}

	public void setActiveBeing(Being activeBeing) {
		
		if (activeBeing!=null)
			this.activeBeing = Optional.of(activeBeing);
		else
			this.activeBeing = Optional.empty();
	}

	public void setCurPlace(Place curPlace) {
		this.curPlace = Optional.ofNullable(curPlace);
	}
	
	public void setLocale(String locale) {
		setLocale(Locale.forLanguageTag(locale));
	}

	public void setLocale(Locale newLocale) {
		
		// If it's changing the locale, reload the messages
		if ((newLocale!=null) && (!messages.getLocale().equals(newLocale))) {
			this.messages = new LocalizedMessages(newLocale);
		}
	}
	
	public String getLocalizedMessage(String key) {
		return this.messages.getMessage(key);
	}
	
	public void clearState() {

		clearBeingInformation();
		
		this.setAuthToken(null);
		this.playerData = Optional.empty();
	}
	
	public void clearBeingInformation() {
		this.activeBeing = Optional.empty();
		this.curPlace = Optional.empty();
	}
	
	
	private String formatMessage(Message m) {
		
		String response = null;
		
		if (m.getSenderCode()!=null) {
			response = String.format("[%s] %s: %s", m.getMessageDate(), m.getSenderName(), m.getContent());
		} else {
			response = String.format("[%s]: %s", m.getMessageDate(), m.getContent());
		}
		
		return response;
	}
	
	private void internalSendMessage(String message, boolean includeCRLF) {
		
		String effectiveMessage = getLocalizedMessage(message) + (includeCRLF ? ClientHelper.CRLF : "");
		
		// Build the message to send over tcp connection		
		org.springframework.messaging.Message<String> clientMessage = 
				MessageBuilder.withPayload(effectiveMessage)
					// .setHeader("headerName", "headerValue")
					.build();
		
		try {
			this.connection.send(clientMessage);
		} catch(Exception e) {
			log.error("Error trying to send message", e);
		}
	}
	
	public void disableEcho() throws Exception {
		internalSendBytes(ECHO_OFF_SEQUENCE);
		this.echoEnabled = false;
	}
	
	public void enableEcho() throws Exception {
		internalSendBytes(ECHO_ON_SEQUENCE);
		this.echoEnabled = true;
	}
	
	private void internalSendBytes(byte[] buffer) throws Exception {
		
		org.springframework.messaging.Message<byte[]> clientMessage = 
				MessageBuilder.withPayload(buffer)
					.build();
		
		this.connection.send(clientMessage);
	}
	
	/**
	 * Sends a message to the client, appending the \r\n terminator
	 * 
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public void sendMessage(String message) {
		internalSendMessage(message, true);
	}
	
	/**
	 * Sends a notification message from the game engine to the client
	 * @param c
	 * @param m
	 * @throws Exception
	 */
	public void sendMessage(Message m) {
		internalSendMessage(formatMessage(m), true);
	}

	/**
	 * Sends a data request message.  Doesn't append the \r\n terminator.
	 * @param c
	 * @param message
	 * @throws Exception
	 */
	public void sendRequestMessage(String message) {
		internalSendMessage(message, false);
	}

	/**
	 * Sends a text file to the client.
	 * @param c
	 * @param filename
	 * @throws IOException 
	 * @throws Exception
	 */
	public void sendFile(String filename) throws IOException  {
		
		File f = new ClassPathResource(filename).getFile();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		try {
			while (reader.ready()) {
				sendMessage(reader.readLine());
			}
		} finally {
			reader.close();
		}
	}

}
