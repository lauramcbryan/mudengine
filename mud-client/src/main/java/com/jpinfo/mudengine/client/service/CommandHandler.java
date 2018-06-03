package com.jpinfo.mudengine.client.service;

import java.util.List;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.api.ApiResult;
import com.jpinfo.mudengine.client.api.MudengineApi;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionary;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.player.Player;

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
	
	public static final String WHOAMI_COMMAND = "whoami";
	public static final String WHEREAMI_COMMAND = "whereami";
	

	@Autowired
	private VerbDictionary verbDictionary;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
	@Autowired
	private MudengineApi api;

	
	/**
	 * That command is used to register a new account.
	 * The user must provide an unique username and an email account where the password will be sent
	 * 
	 * The account will be in PENDING status until ACTIVATE command be issued
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleRegisterCommand(ClientConnection client, CommandState command) throws Exception {

		String username = ClientHelper.getParamValue(command, "username");
		String email = ClientHelper.getParamValue(command, "email");
		String locale = ClientHelper.getParamValue(command, "locale");
		

		ClientHelper.sendMessage(client, "Registering account...");
		
		// Call register in PlayerService
		api.registerPlayer(username, email, locale);

		ClientHelper.sendMessage(client, "Account registered.  Now you must activate your account.\r\nPlease check your email for the activation code");
	}
	
	/**
	 * Change an account password.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handlePasswordCommand(ClientConnection client, CommandState command) throws Exception {

		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

		
		String oldPassword = ClientHelper.getParamValue(command, "oldPassword");
		String newPassword = ClientHelper.getParamValue(command, "newPassword");

		// Call setPassword in PlayerService
		api.setPlayerPassword(playerData.getUsername(), oldPassword, newPassword);
		
		ClientHelper.sendMessage(client, "Password changed.");
		
	}
	
	/**
	 * Activates a PENDING account changing it password.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleActivateCommand(ClientConnection client, CommandState command) throws Exception {

		ClientHelper.sendMessage(client, "Activating account...");

		String username = ClientHelper.getParamValue(command, "username");
		String activationCode = ClientHelper.getParamValue(command, "activationCode");
		String newPassword = ClientHelper.getParamValue(command, "newPassword");

		// Call activateAccount in PlayerService
		api.setPlayerPassword(username, activationCode, newPassword);
		
		ClientHelper.sendMessage(client, "Your account is activated. To create a session, use the <login> command");
	}

	/**
	 * Shows up all the commands available.
	 * If the client isn't authenticated, only unlogged commands will be shown.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleHelpCommand(ClientConnection client, CommandState command) throws Exception {

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
		
	}
	
	/**
	 * Authenticate and initiates a session for the user.
	 * The account must exist and be in ACTIVE status.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleLoginCommand(ClientConnection client, CommandState command) throws Exception {
		String username = ClientHelper.getParamValue(command, "username");				
		String password = ClientHelper.getParamValue(command, "password");
		
		String authToken = api.createSession(username, password, 
				ClientHelper.CLIENT_TYPE, client.getConnection().getHostAddress());
		
		// Updating the authToken
		client.setAuthToken(authToken);

		client.setPlayerData(api.getPlayerDetails(authToken, username));
		
		ClientHelper.sendMessage(client, "Logged in.  Welcome back " + username);
	}
	
	/**
	 * Changes profile information.
	 * So far the only fields that can be changed are email and locale.
	 * 
	 * If the client changes the email, the account goes back to PENDING status
	 * and must be ACTIVATEd again.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleChangeProfileCommand(ClientConnection client, CommandState command) throws Exception {
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

		playerData.setEmail(ClientHelper.getParamValue(command, "email"));
		playerData.setLocale(ClientHelper.getParamValue(command, "locale"));

		// POST /{username}
		ApiResult changedData = api.updatePlayerDetails(client.getAuthToken(), playerData);
		
		client.setAuthToken(changedData.getChangedAuthToken());
		client.setPlayerData(changedData.getUpdatedPlayerData());
	}
	
	/**
	 * Creates a new being for the current account.
	 * The user must be logged.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleCreateBeingCommand(ClientConnection client, CommandState command) throws Exception {

		String beingClass = ClientHelper.getParamValue(command, "beingClass");
		String beingName  = ClientHelper.getParamValue(command, "beingName");
		String worldName = "aforgotten";
		Integer placeCode = 1;

		// If the being class is provided, create the being and set in player session
		if (beingClass!=null) {
			
			ApiResult apiResult = 
					api.createBeing(client.getAuthToken(), client.getPlayerData().get().getUsername(), 
							beingClass, beingName, worldName, placeCode);

			// Update the player info
			client.setPlayerData(apiResult.getUpdatedPlayerData());
			client.setAuthToken(apiResult.getChangedAuthToken());
			
			// Shows up the being list again
			ClientHelper.sendMessage(client, 
					ClientHelper.listAvailableBeings(apiResult.getUpdatedPlayerData(), client.getActiveBeingCode())
				);
			
		} else {
			
			// The being class is not provided, show the being class list
			List<BeingClass> beingClassList = api.getBeingClasses(client.getAuthToken());
			ClientHelper.sendMessage(client,
				ClientHelper.listAvailableBeingClasses(client, beingClassList)
				);
		}
		
	}
	
	/**
	 * Select an existing being for the account.
	 * The being must exist previously.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleSelectBeingCommand(ClientConnection client, CommandState command) throws Exception {
		
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

		Long beingCode =ClientHelper.getParamValue(command, "beingCode", Long.class);
		
		// if the user provided a beingId, assume it.
		// If not, show the being list for that player
		if (beingCode!=null) {

			// Check if the beingCode is one of the available for this player
			if (playerData.getBeingList().stream()
				.noneMatch(d-> d.getBeingCode().equals(beingCode))) {
				
				throw new ClientException("being unknown");
			}
			
			// Set the being as active
			String changedAuthToken= 
					api.setActiveBeing(client.getAuthToken(), 
							playerData.getUsername(), 
							beingCode);

			// Updating the authToken
			client.setAuthToken(changedAuthToken);
			
			Being selectedBeing = api.getBeing(client.getAuthToken(), beingCode);
			
			// Setting the beingCode at client connection
			client.setActiveBeing(selectedBeing);
			
			// Setting the current place at client connection
			client.setCurPlace(api.getPlace(client.getAuthToken(), selectedBeing.getCurPlaceCode()));
			
			// Show being information
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedBeingData(selectedBeing));
			
			// Show place information
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedPlaceData(client.getCurPlace()));
			
		} else {
			
			// No beingCode provided, show me the available beings
			ClientHelper.sendMessage(client, 
				ClientHelper.listAvailableBeings(playerData, client.getActiveBeingCode())
			);
			
		}
	}
	
	/**
	 * Eliminates a being for that account.
	 * 
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleDestroyBeingCommand(ClientConnection client, CommandState command) throws Exception {

		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

		Long beingCode =ClientHelper.getParamValue(command, "beingCode", Long.class);
		
		// if the user provided a beingId, assume it.
		// If not, show the being list for that player
		if (beingCode!=null) {
			
			// Check if the being exist and it's associated with the player
			if (playerData.getBeingList().stream()
				.noneMatch(d-> d.getBeingCode().equals(beingCode))) {
				
				throw new ClientException("being unknown");
			}
			
			// Destroy the being and update the authToken
			ApiResult apiResult = api.destroyBeing(client.getAuthToken(), playerData.getUsername(), beingCode);
			
			// If the currently active beingCode is the same destroyed, erase it
			if ((client.hasBeingSelected()) && (client.getActiveBeing().getBeingCode().equals(beingCode))) {
				client.setActiveBeing(null);
				client.setCurPlace(null);
			}
			
			// Refresh playerData
			client.setPlayerData(apiResult.getUpdatedPlayerData());
			client.setAuthToken(apiResult.getChangedAuthToken());
			
		}
		
		// Shows the being available list
		ClientHelper.sendMessage(client,
				ClientHelper.listAvailableBeings(client.getPlayerData().get(), client.getActiveBeingCode())
			);
		
	}
	
	private void handleWhoAmICommand(ClientConnection client, CommandState command) throws Exception {
		
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException("You must be logged to issue this command" ));

		// Shows up player information
		ClientHelper.sendMessage(client, 
				ClientHelper.returnFormattedPlayerData(playerData, client.getActiveBeingCode()));

		// If there's an active being
		if (client.hasBeingSelected()) {
		
			Being activeBeing = client.getActiveBeing();
			
			// Shows up being information
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedBeingData(activeBeing));
		}
	}
	
	private void handleWhereAmICommand(ClientConnection client, CommandState command) throws Exception {
		
		if (!client.isLogged()) {
			throw new ClientException("You must be logged to issue this command");
		}
		
		if (!client.hasBeingSelected()) {
			throw new ClientException("You must have an active being to issue this command");
		}
		
		ClientHelper.sendMessage(client, 
				ClientHelper.returnFormattedPlaceData(client.getCurPlace()));
	}

	/**
	 * Main method for handling all SYSTEM commands.
	 * These commands are handled internally by the client.
	 * Administrative tasks and simple requests are covered here.
	 * 
	 * @param client - object with player info
	 * @param command - command being processed
	 * @throws Exception
	 */
	public void handleSystemCommand(ClientConnection client, CommandState command) throws Exception {

		try {
		
			switch(command.getCommand().getVerb()) {
			
				case CommandHandler.REGISTER_COMMAND: {
					handleRegisterCommand(client, command);
					break;			
				}
					
				case CommandHandler.ACTIVATE_COMMAND: {
					handleActivateCommand(client, command);
					break;
				}
					
				case CommandHandler.PASSWORD_COMMAND: {
					handlePasswordCommand(client, command);
					break;
				}
				case CommandHandler.QUIT_COMMAND: {
					ClientHelper.sendFile(client,  ClientHelper.GOODBYE_FILE);
					connFactory.closeConnection(client.getConnection().getConnectionId());
					break;
					
				}
				case CommandHandler.HELP_COMMAND: {
					handleHelpCommand(client, command);
					break;
				}
				case CommandHandler.LOGIN_COMMAND: {
					handleLoginCommand(client, command);
					break;
				}
				case CommandHandler.LOGOUT_COMMAND: {
					
					client.setAuthToken(null);
					client.setPlayerData(null);
					client.setActiveBeing(null);
					client.setCurPlace(null);
					
					ClientHelper.sendMessage(client, "Your session was terminated");
					
					break;
				}
				case CommandHandler.CHANGEPROF_COMMAND: {
					handleChangeProfileCommand(client, command);
					break;
				}
				case CommandHandler.CREATEBEING_COMMAND: {
					handleCreateBeingCommand(client, command);
					break;
				}
				case CommandHandler.SELECTBEING_COMMAND: {
					handleSelectBeingCommand(client, command);
					break;
				}
				case CommandHandler.DELETEBEING_COMMAND: {
					handleDestroyBeingCommand(client, command);
					break;
				}
				case CommandHandler.WHOAMI_COMMAND: {
					handleWhoAmICommand(client, command);
					break;
				}
				case CommandHandler.WHEREAMI_COMMAND: {
					handleWhereAmICommand(client, command);
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

	/**
	 * Main method for handling GAME commands.
	 * All these commands generates ACTIONs in game engine.
	 * The output of such ACTIONs can be received by the messages channel
	 * 
	 * @param client - object with player info
	 * @param command - command being processed
	 * @throws Exception
	 */
	public void handleGameCommand(ClientConnection client, CommandState command) throws Exception {

		if (!client.hasBeingSelected()) {
			throw new ClientException("You must be logged to issue this command" );
		}
		
		Long actorCode = client.getActiveBeing().getBeingCode();
		String verb = command.getCommand().getVerb();

		String mediatorCode = ClientHelper.getParamValue(command, "mediatorCode");
		String mediatorType = ClientHelper.getParamValue(command, "mediatorType");
		
		String targetCode = ClientHelper.getParamValue(command, "targetCode");
		String targetType = ClientHelper.getParamValue(command, "targetType");

		
		// Perform the call to the API gateway
		api.insertCommand(client.getAuthToken(), verb, actorCode, 
				Optional.of(mediatorCode), Optional.of(mediatorType), 
				targetCode, targetType);
	}
}
