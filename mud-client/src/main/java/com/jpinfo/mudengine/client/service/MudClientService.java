package com.jpinfo.mudengine.client.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.Command;
import com.jpinfo.mudengine.client.model.CommandParam;
import com.jpinfo.mudengine.client.model.VerbDictionary;
import com.jpinfo.mudengine.client.utils.ClientHelper;

@MessageEndpoint
public class MudClientService {
	
	@Autowired
	private MudClientGateway gateway;
	
	private VerbDictionary verbDictionary;
	
	
	@PostConstruct
	public void initializeVerbDictionary() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		
		verbDictionary = 
					jsonMapper.readValue(
							new ClassPathResource("system-verbs.json").getFile(), 
							new TypeReference<VerbDictionary>() {});
	}
	
	@ServiceActivator(inputChannel="plainRequestChannel")
	public void handleCommand(@Header(name="ip_connectionId") String connectionId, String in) {
		
		// Retrieves the player attached to that connection
		ClientConnection client = gateway.getActiveConnections().get(connectionId);
		
		if (client!=null) {
			
			try {

				// update the state
				updateClientState(client, in);
				
				// If there's a finished command, execute it
				if ((client.getCurCommand()!=null) && (client.getCurCommand().isReady())) {
					
					// handle command
					if (Command.enumCategory.SYSTEM.equals(client.getCurCommand().getCategory())) {
						// handle internal command
						
						
						
					} else {
						// handle game command
						
						
					}
					
				}
			
				
			} catch(ClientException e) {
				try {
					ClientHelper.sendMessage(client, e.getMessage());
				} catch(Exception ex) {
					e.printStackTrace();
					ex.printStackTrace();
				}
				
			} catch(Exception e) {
				
				try {
					ClientHelper.sendMessage(client, "There was an error processing your input.  Please try again.");
				} catch(Exception ex) {
					e.printStackTrace();
					ex.printStackTrace();					
				}
				
			}
			
		} // endif client!=null
		
		
	}
	
	protected void updateClientState(ClientConnection client, String enteredValue) throws Exception {
		
		// Check if there's a command
		if (client.getCurCommand()!=null) {
			
			// update client command
			updateClientCommand(client, enteredValue);
			
		} else {
			// initiate client command
			
			// The first portion of the input is expected to be the command
			String command = enteredValue.split(" ")[0];

			// if found the command, assign.
			// otherwise, respond with error
			Command choosenCommand = 
				verbDictionary.getDictionary().stream()
					.filter(d-> d.getVerb().equals(command))
					.findFirst()
					.orElseThrow(() -> new ClientException("Unknown command"));
			
			
			client.setCurCommand(choosenCommand);
			
			updateClientCommand(client, 
					enteredValue.substring(
							enteredValue.indexOf(" "), 
							enteredValue.length()));
		}
	}
	
	protected void updateClientCommand(ClientConnection client, String enteredValue) throws Exception {
		
		// Verify if there's an active command
		if (client.getCurCommand()!=null) {
			
			// Verify if there's an active parameter
			if (client.getCurParam()!=null) {
				
				// Set the entered value as input for the parameter
				client.getCurParam().setEnteredValue(enteredValue);
				
				if (!client.getCurParam().isValid()) {
					ClientHelper.sendMessage(client, "The value entered is invalid.");
				}
			} 
			else {
				
				// If there's no parameter set, we'll split the enteredValues and put then in the parameters
				String[] arrEnteredValues = enteredValue.split(" ");
				int index = 0;
				
				for(CommandParam curParam: client.getCurCommand().getParameters()) {

					if (index>arrEnteredValues.length)
						break;
					else
						curParam.setEnteredValue(arrEnteredValues[index++]);
				}
			}
			
			// Get the next parameter (or the very same if the value entered is invalid)
			Optional<CommandParam> nextParam = 
				client.getCurCommand().getParameters().stream()
				.filter(e-> !e.isValid())
				.findFirst();
			
			if (nextParam.isPresent()) {
				
				// Set the current parameter
				client.setCurParam(nextParam.get());
				
				// Asks for the value
				ClientHelper.sendMessage(client, nextParam.get().getInputMessage());
			}
		} // endif getCurCommand!=null
		
	} // end updateClientCommand

	protected void handleSystemCommand(ClientConnection client, Command command) {
		
		
		
	}
	
	protected void handleGameCommand(ClientConnection client, Command command) {
		
		Long actorCode = client.getCurrentBeingId();
		String verb = command.getVerb();

		String mediatorCode = getParamValue(command, "mediatorCode");
		String mediatorType = getParamValue(command, "mediatorType");
		
		String targetCode = getParamValue(command, "targetCode");
		String targetType = getParamValue(command, "targetType");

		
		// TODO: Perform the call to the API gateway
		
		
		
	}
	
	
	private String getParamValue(Command command, String key) {

		Optional<CommandParam> foundParam = command.getParameters().stream()
				.filter(d -> d.getName().equals(key))
				.findFirst();
		
		return (foundParam.isPresent() ? foundParam.get().getEffectiveValue(): null);
	}
}
