package com.jpinfo.mudengine.client.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.interfaces.CommandHandler;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.VerbDictionaries;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.action.CommandParam.enumParamTypes;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.player.Player;

@MessageEndpoint
public class MudClientService {
	
	private static final Logger log = LoggerFactory.getLogger(MudClientService.class);
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private VerbDictionaries verbDictionaries;
	
	@Autowired
	@Qualifier("game-commands")
	private CommandHandler gameHandler;
	
	@Autowired
	@Qualifier("system-commands")
	private CommandHandler systemHandler;
	
	@Autowired
	@Qualifier("admin-commands")
	private CommandHandler adminHandler;
		
	
	@ServiceActivator(inputChannel="plainRequestChannel")
	public String handleCommand(@Header(name="ip_connectionId") String connectionId, String in) {
		
		// Retrieves the player attached to that connection
		ClientConnection client = gateway.getActiveConnections().get(connectionId);
		
		if (client!=null) {
			
			// Update the last activity time from that client
			client.setLastActivity(System.currentTimeMillis());
			
			try {

				// update the state
				updateClientState(client, in);
				
				// If there's a finished command, execute it
				if ((client.getCurCommand()!=null) && (client.getCurCommandState().isReady())) {
					
					StringBuilder msg = new StringBuilder();
					
					msg.append("Processing command [")
						.append(client.getCurCommand().getVerb())
						.append("] with parameters: ");

					// Assembling the message with parameter values
					client.getCurCommandState().getParameters().stream().forEach(d-> 
						msg.append(d.getParameter().getName())
							.append("=")
							.append(d.getEffectiveValue())
							.append(";")
					);
					
					msg.append(ClientHelper.CRLF + "Client Token: ").append(client.getAuthToken());
					
					// Just to avoid Sonar Squid rule 52629 
					String dummy = msg.toString();
					log.info(dummy);

					switch(client.getCurCommand().getCategory()) {
					case ADMIN:
						adminHandler.handleCommand(client, client.getCurCommandState());
						break;
					case GAME:
						// handle game command
						gameHandler.handleCommand(client, client.getCurCommandState());
						
						break;
					case SYSTEM:
						// handle internal command
						systemHandler.handleCommand(client, client.getCurCommandState());
						
						break;
					default:
						break;
					
					}
				}
			
				
			} catch(ClientException e) {
				try {
					client.sendMessage(e.getMessage());
				} catch(Exception ex) {
					log.error("General error sending message", ex);
				}
				
			} catch(Exception e) {
				
				try {			
					client.sendMessage(LocalizedMessages.GENERAL_ERROR_MESSAGE);
				} catch(Exception ex) {
					log.error("General error sending message", ex);					
				}
				
			}
			
			// If it's waiting an input
			if (client.getCurCommandState()!=null) {
				
				// return just the input prompt
				return " ";
				
			} else {
				
				// Full Prompt
				Optional<Player> playerData = client.getPlayerData();
				Optional<Being> activeBeing = client.getActiveBeing();
				
				String username = (playerData.isPresent() ? 
									playerData.get().getUsername() : 
									client.getLocalizedMessage(LocalizedMessages.ANONYMOUS_MESSAGE));
				
				String beingName = (activeBeing.isPresent() ? 
									activeBeing.get().getName(): 
									client.getLocalizedMessage(LocalizedMessages.NO_BEING_MESSAGE));
				
				
				return username + "@" + beingName + ":" + (client.isAdmin() ? "# ":"$ ");
			}
			
			
		} // endif client!=null
		
		return " ";
	}
	
	protected void updateClientState(ClientConnection client, String enteredValue) throws Exception {
		
		// Check if there's a command
		if (client.getCurCommand()!=null) {
			
			// update client command
			updateClientCommand(client, enteredValue);
			
		} else {
			// initiate client command

			// If the value entered is empty...
			if (enteredValue.isEmpty())
				return;
			
			// if found the command, assign.
			// otherwise, respond with error
			Command choosenCommand = verbDictionaries
					.getDictionary(client.getMessages().getLocale())
						.getCommand(enteredValue);
			
			if ((choosenCommand.isLogged()) && (!client.isLogged())) {
				throw new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED);
			}
			
			// Set the current command
			switch(choosenCommand.getCategory()) {
			case ADMIN:
				client.setCurCommandState(adminHandler.initializeCommand(client, choosenCommand));
				break;
			case GAME:
				client.setCurCommandState(gameHandler.initializeCommand(client, choosenCommand));
				break;
			case SYSTEM:
				client.setCurCommandState(systemHandler.initializeCommand(client, choosenCommand));
				break;
			default:
				throw new ClientException(LocalizedMessages.COMMAND_NOT_SUPPORTED);
			}
			
			String remainingCommand = enteredValue
					.substring(choosenCommand.getVerb().length(), enteredValue.length())
					.trim();
			
			updateClientCommand(client, remainingCommand);
		}
	}
	
	protected void updateClientCommand(ClientConnection client, String enteredValue) throws Exception {
		
		// Verify if there's an active command
		if (client.getCurCommand()!=null) {
			
			// Verify if there's an active parameter
			if (client.getCurParam()!=null) {
				updateCurParameter(client, client.getCurParamState(), enteredValue);
			} 
			else {
				
				if ((enteredValue!=null) && (!enteredValue.isEmpty())) {
					
					// If there's no parameter set, we'll split the enteredValues and put then in the parameters
					String[] arrEnteredValues = enteredValue.split("\\s+");
					int index = 0;
					
					for(CommandParamState curParam: client.getCurCommandState().getParameters()) {
	
						if (index>=arrEnteredValues.length)
							break;
						else
							curParam.setEnteredValue(arrEnteredValues[index++]);
					}
				}
			}
			
			getNextParameter(client);
			
			
		} // endif getCurCommand!=null
		
	} // end updateClientCommand
	
	private void updateCurParameter(ClientConnection client, CommandParamState param, String enteredValue) throws Exception {
		
		// Set the entered value as input for the parameter
		param.setEnteredValue(enteredValue);
		
		if (!param.isValid()) {
			client.sendMessage(LocalizedMessages.COMMAND_INVALID_PARAMETER);
		} else {
			
			// if the value just entered was from a secure field, turns on the echo
			if (client.getCurParam().getType().equals(enumParamTypes.SECURE_STRING)) {
				client.enableEcho();
			} 
		}
	}
	
	private void getNextParameter(ClientConnection client) throws Exception {

		// Get the next parameter (or the very same if the value entered is invalid)
		Optional<CommandParamState> nextParam = client.getCurCommandState().getNextParameter(); 
		
		if (nextParam.isPresent()) {
			
			CommandParamState param = nextParam.get();
			
			// Set the current parameter
			client.setCurParamState(param);
			
			// Asks for the value
			client.sendRequestMessage(param.getInputMessage());
			
			// if it's a secure field, turns off the echo
			if (param.getParameter().getType().equals(enumParamTypes.SECURE_STRING)) {
				client.disableEcho();
			}
		} 
		
	}

}
