package com.jpinfo.mudengine.client.service;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandParamState;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionary;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.player.Session;

@Component
public class CommandHandler {
	
	public static final String REGISTER_COMMAND = "register";
	public static final String CHANGEPROF_COMMAND = "change profile";
	public static final String ACTIVATE_COMMAND = "activate account";
	public static final String PASSWORD_COMMAND = "change password";
	public static final String QUIT_COMMAND = "quit";
	public static final String HELP_COMMAND = "help";
	public static final String LOGIN_COMMAND = "login";
	public static final String LOGOUT_COMMAND = "logout";
	public static final String CREATEBEING_COMMAND = "create being";
	public static final String SELECTBEING_COMMAND = "select being";
	public static final String DELETEBEING_COMMAND = "destroy being";
	

	@Autowired
	private VerbDictionary verbDictionary;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
	@Autowired
	private MudengineApi api;

	
	public void handleSystemCommand(ClientConnection client, CommandState command) throws Exception {

		try {
		
			switch(command.getCommand().getVerb()) {
			
			case CommandHandler.REGISTER_COMMAND: {

				String username = getParamValue(command, "username");
				String email = getParamValue(command, "email");
				String locale = getParamValue(command, "locale");
				

				ClientHelper.sendMessage(client, "Registering account...");
				
				// Call register in PlayerService
				api.registerPlayer(username, email, locale);

				ClientHelper.sendMessage(client, "Account registered.  Now you must activate your account.\r\nPlease check your email for the activation code");
				
				break;			
			}
				
			case CommandHandler.ACTIVATE_COMMAND: {
				ClientHelper.sendMessage(client, "Activating account...");

				String username = getParamValue(command, "username");
				String activationCode = getParamValue(command, "activationCode");
				String newPassword = getParamValue(command, "newPassword");

				// Call activateAccount in PlayerService
				api.setPlayerPassword(username, activationCode, newPassword);
				
				ClientHelper.sendMessage(client, "Your account is activated. To create a session, use the <login> command");
				
				
				break;
			}
				
			case CommandHandler.PASSWORD_COMMAND: {
			
				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				
				String oldPassword = getParamValue(command, "oldPassword");
				String newPassword = getParamValue(command, "newPassword");

				// Call setPassword in PlayerService
				api.setPlayerPassword(playerData.getUsername(), oldPassword, newPassword);
				
				ClientHelper.sendMessage(client, "Password changed.");
				
				break;
			}
			case CommandHandler.QUIT_COMMAND: {
				
				ClientHelper.sendFile(client,  ClientHelper.GOODBYE_FILE);
				
				connFactory.closeConnection(client.getConnection().getConnectionId());
				
				break;
				
			}
			case CommandHandler.HELP_COMMAND: {
				
				ClientHelper.sendMessage(client,  "\r\nAvailable commands:\r\n ");
				
				verbDictionary.getDictionary().stream().forEach(d-> {
					
					if (!d.isLogged() || client.isLogged()) {
						
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
					}
				});
				
				break;
			}
			case CommandHandler.LOGIN_COMMAND: {
				
				String username = getParamValue(command, "username");				
				String password = getParamValue(command, "password");
				
				String authToken = api.createSession(username, password, 
						ClientHelper.CLIENT_TYPE, client.getConnection().getHostAddress());
				
				// Updating the sessionData
				client.setAuthToken(authToken);
				client.setPlayerSession(api.getSession(authToken, username));
				client.setPlayerData(api.getPlayerDetails(authToken, username));
				
				ClientHelper.sendMessage(client, "Logged in.  Welcome back " + username);
				
				
				break;
			}
			case CommandHandler.LOGOUT_COMMAND: {
				
				client.setAuthToken(null);
				client.setPlayerSession(null);
				client.setPlayerData(null);
				
				ClientHelper.sendMessage(client, "Your session was terminated");
				
				break;
			}
			case CommandHandler.CHANGEPROF_COMMAND: {

				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				playerData.setEmail(getParamValue(command, "email"));
				playerData.setLocale(getParamValue(command, "locale"));

				// POST /{username}
				Player changedPlayerData = api.updatePlayerDetails(client.getAuthToken(), playerData);
				
				client.setPlayerData(changedPlayerData);

				
				break;
			}
			case CommandHandler.CREATEBEING_COMMAND: {
				
				String beingClass = getParamValue(command, "beingClass");
				String beingName  = getParamValue(command, "beingName");
				String worldName = "aforgotten";
				Integer placeCode = 1;

				// If the being class is provided, create the being and set in player session
				if (beingClass!=null) {
					
					Session changedSessionData = 
							api.createBeing(client.getAuthToken(), client.getPlayerData().get().getUsername(), 
									beingClass, beingName, worldName, placeCode);
						
						client.setPlayerSession(changedSessionData);
				} else {
					
					// The being class is not provided, show the being class list
					
					List<BeingClass> beingClassList = api.getBeingClasses(client.getAuthToken());
					
					ClientHelper.sendMessage(client, "Available classes: \r\n");
					
					beingClassList.forEach(d -> {
						
						StringBuffer m = new StringBuffer();
						
						m.append(d.getBeingClass())
							.append(" - ")
							.append(d.getName());
						
						try {
							ClientHelper.sendMessage(client, m.toString());
						} catch(Exception e) {
							// Proceed to the next one
						}
					});
				}
				
				
				
				break;
			}
			case CommandHandler.SELECTBEING_COMMAND: {

				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				// if the user provided a beingId, assume it.
				// If not, show the being list for that player
				Long beingCode =getParamValue(command, "beingCode", Long.class);
				
				if (beingCode!=null) {
					
					playerData.getBeingList().stream()
						.filter(d-> d.getBeingCode().equals(beingCode))
						.findFirst()
						.orElseThrow(() -> new ClientException("being unknown"));
					
					Session changedSessionData = api.setActiveBeing(client.getAuthToken(), playerData.getUsername(), beingCode);
					
					client.setPlayerSession(changedSessionData);
					
				} else {
					listAvailableBeing(client);
				}
				
				break;
			}
			case CommandHandler.DELETEBEING_COMMAND: {

				Player playerData = 
						client.getPlayerData()
							.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

				// if the user provided a beingId, assume it.
				// If not, show the being list for that player
				Long beingCode =getParamValue(command, "beingCode", Long.class);
				
				if (beingCode!=null) {
					
					// Check if the being exist and it's associated with the player
					if (playerData.getBeingList().stream()
						.noneMatch(d-> d.getBeingCode().equals(beingCode))) {
						
						throw new ClientException("being unknown");
					}
					
					Session changedSessionData = api.destroyBeing(client.getAuthToken(), playerData.getUsername(), beingCode);
					
					client.setPlayerSession(changedSessionData);
					
					// Refresh playerData
					client.setPlayerData(
							api.getPlayerDetails(
									client.getAuthToken(), 
									playerData.getUsername()));
					
					
				} else {
					listAvailableBeing(client);
				}
				
				break;
			}
			
			default: {
				ClientHelper.sendMessage(client, "Unsupported command" );
			}
			
			}
		} finally {
			client.setCurCommandState(null);
		}
	}
	
	public void handleGameCommand(ClientConnection client, CommandState command) throws Exception {
		
		Session sessionData = client.getPlayerSession()
				.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));
		
		Long actorCode = sessionData.getBeingCode();
		String verb = command.getCommand().getVerb();

		String mediatorCode = getParamValue(command, "mediatorCode");
		String mediatorType = getParamValue(command, "mediatorType");
		
		String targetCode = getParamValue(command, "targetCode");
		String targetType = getParamValue(command, "targetType");

		
		// Perform the call to the API gateway
		api.insertCommand(client.getAuthToken(), verb, actorCode, 
				Optional.of(mediatorCode), Optional.of(mediatorType), 
				targetCode, targetType);
	}
	

	private <T> T getParamValue(CommandState command, String key, Class<T> returnClass) throws ClientException  {
		
		String paramValue = getParamValue(command, key);
		
		if (paramValue!=null) {
			try {
				return returnClass.getConstructor(String.class).newInstance(paramValue);
			} catch (Exception e) {
				
				throw new ClientException("System error retrieving parameter values.");
			}
		}
		
		
		return null;
		
	}	
	
	private String getParamValue(CommandState command, String key) {

		Optional<CommandParamState> foundParam = command.getParameters().stream()
				.filter(d -> d.getParameter().getName().equals(key))
				.findFirst();
		
		return (foundParam.isPresent() ? foundParam.get().getEffectiveValue(): null);
	}
	
	private void listAvailableBeing(ClientConnection client) throws Exception {
		
		Player playerData = client.getPlayerData()
				.orElseThrow(() -> new ClientException("You must be logged to issue this command"));
		
		ClientHelper.sendMessage(client, "Available beings: \r\n");
		
		playerData.getBeingList().stream()
			.forEach(d -> {
				
				try {
					
					StringBuffer m = new StringBuffer();
					
					m.append(d.getBeingCode())
						.append(" - ").append(d.getBeingName())
						.append(" (").append(d.getBeingClass()).append(") ");
				
					ClientHelper.sendMessage(client, m.toString());
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
		});
		
		if (playerData.getBeingList().isEmpty()) {
			ClientHelper.sendMessage(client, "\r\n--- None \r\n");
		}
	}

}
