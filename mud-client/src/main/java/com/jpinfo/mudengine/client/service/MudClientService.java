package com.jpinfo.mudengine.client.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionary;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.action.Command;

@MessageEndpoint
public class MudClientService {
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private VerbDictionary verbDictionary;
	
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
					
					StringBuffer msg = new StringBuffer();
					
					msg.append("Processing command [")
						.append(client.getCurCommand().getVerb())
						.append("] with parameters: ");

					// Assembling the message with parameter values
					client.getCurCommandState().getParameters().stream().forEach(d-> {
						msg.append(d.getParameter().getName())
							.append("=")
							.append(d.getEffectiveValue())
							.append(";");
					});
					
					System.out.println(msg.toString());

					
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
					ex.printStackTrace();
				}
				
			} catch(Exception e) {
				
				try {
					e.printStackTrace();					
					ClientHelper.sendMessage(client, "There was an error processing your input.  Please try again.");
				} catch(Exception ex) {
					ex.printStackTrace();					
				}
				
			}
			
		} // endif client!=null
		
		
		return "#";
		
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
			CommandState choosenCommand = verbDictionary.getCommand(enteredValue);
			
			if ((choosenCommand.getCommand().isLogged()) && (!client.isLogged())) {
				throw new ClientException("You must be logged to issue this command.");
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
					ClientHelper.sendMessage(client, "The value entered is invalid.");
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
