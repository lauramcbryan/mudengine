package com.jpinfo.mudengine.client.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionaries;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;
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
	private CommandHandler handler;
		
	
	@ServiceActivator(inputChannel="plainRequestChannel")
	public String handleCommand(@Header(name="ip_connectionId") String connectionId, String in) {
		
		// Retrieves the player attached to that connection
		ClientConnection client = gateway.getActiveConnections().get(connectionId);
		
		if (client!=null) {
			
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

					
					// handle command
					if (Command.enumCategory.SYSTEM.equals(client.getCurCommand().getCategory())) {

						// handle internal command
						handler.handleSystemCommand(client, client.getCurCommandState());
						
					} else {
						// handle game command
						handler.handleGameCommand(client, client.getCurCommandState());
					}
					
				}
			
				
			} catch(ClientException e) {
				try {
					ClientHelper.sendMessage(client, e.getMessage());
				} catch(Exception ex) {
					log.error("General error sending message", ex);
				}
				
			} catch(Exception e) {
				
				try {			
					ClientHelper.sendMessage(client, LocalizedMessages.GENERAL_ERROR_MESSAGE);
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
				
				
				return username + "@" + beingName + ":$ ";
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
			CommandState choosenCommand = verbDictionaries
					.getDictionary(client.getLocale())
						.getCommand(enteredValue);
			
			if ((choosenCommand.getCommand().isLogged()) && (!client.isLogged())) {
				throw new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED);
			}
			
			// Set the current command
			client.setCurCommandState(choosenCommand);
			
			String remainingCommand = enteredValue
					.substring(choosenCommand.getCommand().getVerb().length(), enteredValue.length())
					.trim();
			
			updateClientCommand(client, remainingCommand);
		}
	}
	
	protected void updateClientCommand(ClientConnection client, String enteredValue) throws Exception {
		
		// Verify if there's an active command
		if (client.getCurCommand()!=null) {
			
			// Verify if there's an active parameter
			if (client.getCurParam()!=null) {
				
				// Set the entered value as input for the parameter
				client.getCurParamState().setEnteredValue(enteredValue);
				
				if (!client.getCurParamState().isValid()) {
					ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_INVALID_PARAMETER);
				}
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
			
			// Get the next parameter (or the very same if the value entered is invalid)
			Optional<CommandParamState> nextParam = client.getCurCommandState().getNextParameter(); 
			
			if (nextParam.isPresent()) {
				
				// Set the current parameter
				client.setCurParamState(nextParam.get());
				
				// Asks for the value
				ClientHelper.sendRequestMessage(client, nextParam.get().getParameter().getInputMessage());
			}
		} // endif getCurCommand!=null
		
	} // end updateClientCommand

}
