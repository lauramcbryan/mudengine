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
import com.jpinfo.mudengine.client.model.VerbDictionaries;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingClass;
import com.jpinfo.mudengine.common.player.Player;

@Component
public class CommandHandler {
	
	public static final int REGISTER_COMMAND = 901;
	public static final int CHANGEPROF_COMMAND = 902;
	public static final int ACTIVATE_COMMAND = 903;
	public static final int PASSWORD_COMMAND = 904;
	public static final int QUIT_COMMAND = 905;
	public static final int HELP_COMMAND = 906;
	public static final int LOGIN_COMMAND = 907;
	public static final int LOGOUT_COMMAND = 908;
	public static final int CREATEBEING_COMMAND = 909;
	public static final int SELECTBEING_COMMAND = 910;
	public static final int DELETEBEING_COMMAND = 911;
	
	public static final int WHOAMI_COMMAND = 912;
	public static final int WHEREAMI_COMMAND = 913;
	public static final int LOCALE_COMMAND = 914;
	

	@Autowired
	private VerbDictionaries verbDictionaries;
	
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
		

		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_REGISTER_START);
		
		// Call register in PlayerService
		api.registerPlayer(username, email, locale);

		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_REGISTER_OK);
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
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

		
		String oldPassword = ClientHelper.getParamValue(command, "oldPassword");
		String newPassword = ClientHelper.getParamValue(command, "newPassword");

		// Call setPassword in PlayerService
		api.setPlayerPassword(playerData.getUsername(), oldPassword, newPassword);
		
		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_PASSWORD_OK);
		
	}
	
	/**
	 * Activates a PENDING account changing it password.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleActivateCommand(ClientConnection client, CommandState command) throws Exception {

		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_ACTIVATE_START);

		String username = ClientHelper.getParamValue(command, "username");
		String activationCode = ClientHelper.getParamValue(command, "activationCode");
		String newPassword = ClientHelper.getParamValue(command, "newPassword");

		// Call activateAccount in PlayerService
		api.setPlayerPassword(username, activationCode, newPassword);
		
		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_ACTIVATE_OK);
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

		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_HELP_START);
		
		verbDictionaries
			.getDictionary(client.getLocale())
				.getCommandList().stream().forEach(d-> {
			
			if (!d.isLogged() || client.isLogged()) {
				
				StringBuilder msg = new StringBuilder();
				msg
					.append(d.getVerb())
					.append(" -> ")
					.append(d.getDescription())
					.append(client.getLocalizedMessage(LocalizedMessages.COMMAND_HELP_USAGE))
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

		// Login information
		client.setPlayerData(api.getPlayerDetails(authToken, username));
		
		ClientHelper.sendMessage(client, client.getLocalizedMessage(LocalizedMessages.COMMAND_LOGIN_OK) + username);
	}
	
	/**
	 * Closes the session (doesn't close the connection).
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleLogoutCommand(ClientConnection client, CommandState command) throws Exception {

		client.clearState();
		
		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_LOGOUT_OK);
		
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
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

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
			
			if (client.getPlayerData().isPresent()) {
			
				ApiResult apiResult = 
						api.createBeing(client.getAuthToken(), client.getPlayerData().get().getUsername(), 
								beingClass, beingName, worldName, placeCode);
	
				// Update the player info
				client.setPlayerData(apiResult.getUpdatedPlayerData());
				client.setAuthToken(apiResult.getChangedAuthToken());
				
				// Shows up the being list again
				ClientHelper.sendMessage(client, 
						ClientHelper.listAvailableBeings(client, apiResult.getUpdatedPlayerData(), client.getActiveBeingCode())
					);
			}
			
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
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

		Long beingCode =ClientHelper.getParamValue(command, "beingCode", Long.class);
		
		// if the user provided a beingId, assume it.
		// If not, show the being list for that player
		if (beingCode!=null) {

			// Check if the beingCode is one of the available for this player
			if (playerData.getBeingList().stream()
				.noneMatch(d-> d.getBeingCode().equals(beingCode))) {
				
				throw new ClientException(LocalizedMessages.COMMAND_UNKNOWN_BEING);
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
					ClientHelper.returnFormattedBeingData(client, selectedBeing));
			
			// Show place information
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedPlaceData(client, client.getCurPlace().get()));
			
		} else {
			
			// No beingCode provided, show me the available beings
			ClientHelper.sendMessage(client, 
				ClientHelper.listAvailableBeings(client, playerData, client.getActiveBeingCode())
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
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED ));

		Long beingCode =ClientHelper.getParamValue(command, "beingCode", Long.class);
		
		// if the user provided a beingId, assume it.
		// If not, show the being list for that player
		if (beingCode!=null) {
			
			// Check if the being exist and it's associated with the player
			if (playerData.getBeingList().stream()
				.noneMatch(d-> d.getBeingCode().equals(beingCode))) {
				
				throw new ClientException(LocalizedMessages.COMMAND_UNKNOWN_BEING);
			}
			
			// Destroy the being and update the authToken
			ApiResult apiResult = api.destroyBeing(client.getAuthToken(), playerData.getUsername(), beingCode);
			
			// If the currently active beingCode is the same destroyed, erase it
			client.getActiveBeing().ifPresent(d -> {

				// If the currently active beingCode is the same destroyed, erase it				
				if (d.getBeingCode().equals(beingCode)) {
					client.clearBeingInformation();
				}
				
				
			});
			
			// Refresh playerData
			client.setPlayerData(apiResult.getUpdatedPlayerData());
			client.setAuthToken(apiResult.getChangedAuthToken());
			
		}
		
		// Shows the being available list
		ClientHelper.sendMessage(client,
				ClientHelper.listAvailableBeings(client, client.getPlayerData().get(), client.getActiveBeingCode())
			);
		
	}
	
	/**
	 * Returns information about the player logged (and active being, if it exists)
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleWhoAmICommand(ClientConnection client, CommandState command) throws Exception {
		
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED ));
		

		// Shows up player information
		ClientHelper.sendMessage(client, 
				ClientHelper.returnFormattedPlayerData(client, playerData, client.getActiveBeingCode()));

		// If there's an active being
		if (client.getActiveBeing().isPresent()) {
			
			// Shows up being information			
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedBeingData(client, client.getActiveBeing().get()));
		}
	}
	
	/**
	 * Returns information about the current place of active being.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleWhereAmICommand(ClientConnection client, CommandState command) throws Exception {
		
		if (client.getCurPlace().isPresent()) {
			
			ClientHelper.sendMessage(client, 
					ClientHelper.returnFormattedPlaceData(client, client.getCurPlace().get()));
			
		} else {
			throw new ClientException(LocalizedMessages.COMMAND_NO_BEING);
		}
		
	}
	
	private void handleLocaleCommand(ClientConnection client, CommandState command) throws Exception {
		
		client.setLocale(ClientHelper.getParamValue(command, "locale"));
		
		ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_LOCALE_OK);
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
		
			switch(command.getCommand().getCommandId()) {
			
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
					handleLogoutCommand(client, command);
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
				case CommandHandler.LOCALE_COMMAND: {
					handleLocaleCommand(client, command);
					break;
				}
				
				default: {
					ClientHelper.sendMessage(client, LocalizedMessages.COMMAND_NOT_SUPPORTED);
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
			throw new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED );
		}
		
		Long actorCode = client.getActiveBeing().get().getBeingCode();
		String mediatorCode = ClientHelper.getParamValue(command, "mediatorCode");
		String targetCode = ClientHelper.getParamValue(command, "targetCode");
		Integer commandId = command.getCommand().getCommandId();
		
		
		// Perform the call to the API gateway
		api.insertCommand(client.getAuthToken(), commandId, actorCode, 
				Optional.ofNullable(mediatorCode), targetCode);
	}
}
