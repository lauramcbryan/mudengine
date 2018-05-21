package com.jpinfo.mudengine.client.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.client.MudClientGateway;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionary;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.action.Command;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.PlayerBeing;
import com.jpinfo.mudengine.common.player.Session;

@MessageEndpoint
public class MudClientService {
	
	@Autowired
	private MudClientGateway gateway;
	
	@Autowired
	private VerbDictionary verbDictionary;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
	
	@Bean
	public VerbDictionary initializeVerbDictionary() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		
		VerbDictionary verbDictionary = 
					jsonMapper.readValue(
							new ClassPathResource("system-verbs.json").getFile(), 
							new TypeReference<VerbDictionary>() {});
		
		return verbDictionary;
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
						handleSystemCommand(client, client.getCurCommandState());
						
					} else {
						// handle game command
						handleGameCommand(client, client.getCurCommandState());
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
				ClientHelper.sendMessage(client, nextParam.get().getParameter().getInputMessage());
			}
		} // endif getCurCommand!=null
		
	} // end updateClientCommand

	protected void handleSystemCommand(ClientConnection client, CommandState command) throws Exception {
		
		

		try {
		
			switch(command.getCommand().getVerb()) {
			
			case ClientHelper.REGISTER_COMMAND: {

				String username = getParamValue(command, "username");
				String email = getParamValue(command, "email");
				String locale = getParamValue(command, "locale");
				

				ClientHelper.sendMessage(client, "Registering account...");
				
				// TODO Call register in PlayerService
				// PUT /player/{username}?email=aaa&locale=xxx
				

				
				break;			
			}
				
			case ClientHelper.ACTIVATE_COMMAND:
				ClientHelper.sendMessage(client, "Activating account...");
				
			case ClientHelper.PASSWORD_COMMAND: {
				
				String activationCode = getParamValue(command, "activationCode");
				String newPassword = getParamValue(command, "newPassword");

				// TODO Call activateAccount in PlayerService
				
				// POST /player/{username}/password?activationCode=aaaaa&newPassword=bbbbb
				
				ClientHelper.sendMessage(client, "Your account is activated. To create a session, use the <login> command");
				
				break;
			}
			case ClientHelper.QUIT_COMMAND: {
				
				ClientHelper.sendFile(client,  ClientHelper.GOODBYE_FILE);
				
				connFactory.closeConnection(client.getConnection().getConnectionId());
				
				break;
				
			}
			case ClientHelper.HELP_COMMAND: {
				
				ClientHelper.sendMessage(client,  "\r\nAvailable commands:\r\n ");
				
				verbDictionary.getDictionary().stream().forEach(d-> {
					
					StringBuffer msg = new StringBuffer();
					msg
						.append(d.getVerb())
						.append(" -> ")
						.append(d.getDescription())
						.append("\r\nUsage: ")
						.append(d.getUsage())
						.append("\r\n");
					
					try {
						ClientHelper.sendMessage(client, msg.toString());
					} catch (Exception e) {
						
						// Go to the next one
					}
				});
				
				break;
			}
			case ClientHelper.LOGIN_COMMAND: {
				
				String username = getParamValue(command, "username");				
				String password = getParamValue(command, "password");
				
				// PUT /{username}/session?password=aaaaa
				
				break;
			}
			case ClientHelper.LOGOUT_COMMAND: {
				
				client.setAuthToken(null);
				client.setPlayerSession(null);
				client.setPlayerData(null);
				
				ClientHelper.sendMessage(client, "Your session was terminated");
				
				break;
			}
			case ClientHelper.CHANGEPROF_COMMAND: {

				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				playerData.setEmail(getParamValue(command, "email"));
				playerData.setLocale(getParamValue(command, "locale"));

				// POST /{username}
				

				
				break;
			}
			case ClientHelper.CREATEBEING_COMMAND: {
				
				Session sessionData = client.getPlayerSession()
						.orElseThrow(() -> new ClientException("You must be logged to issue this command"));
				
				// PUT /player/{username}/being?username=&beingClass=&beingName=&worldName=&placeCode=
				
				String beingClass;
				String beingName;
				String worldName;
				Integer placeCode;
				
				break;
			}
			case ClientHelper.SELECTBEING_COMMAND: {

				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				Session sessionData = client.getPlayerSession()
						.orElseThrow(() -> new ClientException("You must be logged to issue this command"));
				
				// if the user provided a beingId, assume it.
				// If not, show the being list for that player
				Long beingCode =Long.valueOf(getParamValue(command, "beingCode"));
				
				if (beingCode!=null) {
					
					PlayerBeing selectedBeing = 
						playerData.getBeingList().stream()
							.filter(d-> d.getBeingCode().equals(beingCode))
							.findFirst()
							.orElseThrow(() -> new ClientException("being unknown"));
					
					sessionData.setBeingCode(beingCode);
					
				} else {
					listAvailableBeing(client);
				}
				
				// POST /player/{username}/session/being/{beingCode}
				
				break;
			}
			case ClientHelper.DELETEBEING_COMMAND: {

				// if the user provided a beingId, assume it.
				// If not, show the being list for that player
				
				// DELETE /player/{username}/being/{beingCode}
				
				break;
			}
			
			}
		} finally {
			client.setCurCommandState(null);
		}
	}
	
	protected void handleGameCommand(ClientConnection client, CommandState command) throws Exception {
		
		Session sessionData = client.getPlayerSession()
				.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));
		
		Long actorCode = sessionData.getBeingCode();
		String verb = command.getCommand().getVerb();

		String mediatorCode = getParamValue(command, "mediatorCode");
		String mediatorType = getParamValue(command, "mediatorType");
		
		String targetCode = getParamValue(command, "targetCode");
		String targetType = getParamValue(command, "targetType");

		
		// TODO: Perform the call to the API gateway
		
		
		
	}
	
	
	private String getParamValue(CommandState command, String key) {

		Optional<CommandParamState> foundParam = command.getParameters().stream()
				.filter(d -> d.getParameter().getName().equals(key))
				.findFirst();
		
		return (foundParam.isPresent() ? foundParam.get().getEffectiveValue(): null);
	}
	
	private void listAvailableBeing(ClientConnection client) throws ClientException {
		
		Player playerData = client.getPlayerData()
				.orElseThrow(() -> new ClientException("You must be logged to issue this command"));
		
		playerData.getBeingList().stream()
			.forEach(d -> {
				
				try {
				
					ClientHelper.sendMessage(client, "Available beings: \r\n");
					
					StringBuffer m = new StringBuffer();
					
					m.append(d.getBeingCode())
						.append("- ").append(d.getBeingName())
						.append(" (").append(d.getBeingClass()).append(") ");
				
					ClientHelper.sendMessage(client, m.toString());
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
		});
	}
}
