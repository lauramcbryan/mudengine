package com.jpinfo.mudengine.client.handlers;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.api.ApiResult;
import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.client.model.VerbDictionaries;
import com.jpinfo.mudengine.client.utils.ClientHelper;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command.enumCategory;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.player.Player;

@Component
@Qualifier("system-commands")
public class SystemCommandHandler extends BaseCommandHandler {
	
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
	public static final int EXIT_COMMAND = 915;

	private static final Logger log = LoggerFactory.getLogger(SystemCommandHandler.class);

	@Autowired
	private VerbDictionaries verbDictionaries;
	
	@Autowired
	private TcpNetServerConnectionFactory connFactory;
	
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
	private void handleRegisterCommand(ClientConnection client, CommandState command) throws ClientException {

		String username = command.getParamValue("username");
		String email = command.getParamValue("email");
		String locale = command.getParamValue("locale");
		

		client.sendMessage(LocalizedMessages.COMMAND_REGISTER_START);
		
		// Call register in PlayerService
		api.registerPlayer(username, email, locale);

		client.sendMessage(LocalizedMessages.COMMAND_REGISTER_OK);
	}
	
	/**
	 * Change an account password.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handlePasswordCommand(ClientConnection client, CommandState command) throws ClientException {

		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

		
		String oldPassword = command.getParamValue("oldPassword");
		String newPassword = command.getParamValue("newPassword");

		// Call setPassword in PlayerService
		api.setPlayerPassword(playerData.getUsername(), oldPassword, newPassword);
		
		client.sendMessage(LocalizedMessages.COMMAND_PASSWORD_OK);
		
	}
	
	/**
	 * Activates a PENDING account changing it password.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleActivateCommand(ClientConnection client, CommandState command) throws ClientException {

		client.sendMessage(LocalizedMessages.COMMAND_ACTIVATE_START);

		String username = command.getParamValue("username");
		String activationCode = command.getParamValue("activationCode");
		String newPassword = command.getParamValue("newPassword");

		// Call activateAccount in PlayerService
		api.setPlayerPassword(username, activationCode, newPassword);
		
		client.sendMessage(LocalizedMessages.COMMAND_ACTIVATE_OK);
	}

	/**
	 * Shows up all the commands available.
	 * If the client isn't authenticated, only unlogged commands will be shown.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleHelpCommand(ClientConnection client) {

		client.sendMessage(LocalizedMessages.COMMAND_HELP_START);
		
		verbDictionaries
			.getDictionary(client.getMessages().getLocale())
				.getCommandList().stream().forEach(d-> {
			
			if (
					(!d.isLogged() || client.isLogged()) && 
					(client.isAdmin() || !d.getCategory().equals(enumCategory.ADMIN))
				) {
				
				StringBuilder msg = new StringBuilder();
				msg
					.append(d.getVerb())
					.append(" -> ")
					.append(d.getDescription())
					.append(client.getLocalizedMessage(LocalizedMessages.COMMAND_HELP_USAGE))
					.append(d.getUsage())
					.append("\r\n");
				
				try {
					client.sendMessage(msg.toString());
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
	private void handleLoginCommand(ClientConnection client, CommandState command) throws ClientException {
		String username = command.getParamValue("username");				
		String password = command.getParamValue("password");
		
		String authToken = api.createSession(username, password, 
				ClientHelper.CLIENT_TYPE, client.getConnection().getHostAddress());
		
		// Updating the authToken
		client.setAuthToken(authToken);

		// Login information
		client.setPlayerData(api.getPlayerDetails(authToken, username));
		
		client.sendMessage(client.getLocalizedMessage(LocalizedMessages.COMMAND_LOGIN_OK) + username);
	}
	
	/**
	 * Closes the session (doesn't close the connection).
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleLogoutCommand(ClientConnection client) {

		client.clearState();
		
		client.sendMessage(LocalizedMessages.COMMAND_LOGOUT_OK);
		
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
	private void handleChangeProfileCommand(ClientConnection client, CommandState command) throws ClientException {
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

		playerData.setEmail(command.getParamValue("email"));
		playerData.setLocale(command.getParamValue("locale"));

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
	private void handleCreateBeingCommand(ClientConnection client, CommandState command) throws ClientException {

		String beingClass = command.getParamValue("beingClass");
		String beingName  = command.getParamValue("beingName");
		String worldName = "aforgotten";
		Integer placeCode = 1;

		// If the being class is provided, create the being and set in player session
		Optional<Player> playerData = client.getPlayerData();
		
		if (playerData.isPresent()) {
		
			ApiResult apiResult = 
					api.createBeing(client.getAuthToken(), playerData.get().getUsername(), 
							beingClass, beingName, worldName, placeCode);

			// Update the player info
			client.setPlayerData(apiResult.getUpdatedPlayerData());
			client.setAuthToken(apiResult.getChangedAuthToken());
			
			// Shows up the being list again
			client.sendMessage(
					ClientHelper.returnFormattedPlayerData(client, apiResult.getUpdatedPlayerData(), client.getActiveBeingCode()));
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
	private void handleSelectBeingCommand(ClientConnection client, CommandState command) throws ClientException {
		
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED));

		Long beingCode =command.getParamValue("beingCode", Long.class);
		
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
			Place curPlace = api.getPlace(client.getAuthToken(), selectedBeing.getCurPlaceCode());
			client.setCurPlace(curPlace);
			
			// Show being information
			client.sendMessage(
					ClientHelper.returnFormattedBeingData(client, selectedBeing));
			
			// Show place information
			client.sendMessage(
					ClientHelper.returnFormattedPlaceData(client, curPlace));
			
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
	private void handleDestroyBeingCommand(ClientConnection client, CommandState command) throws ClientException {

		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED ));

		Long beingCode =command.getParamValue("beingCode", Long.class);
		
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
				if (d.getCode().equals(beingCode)) {
					client.clearBeingInformation();
				}
				
				
			});
			
			// Refresh playerData
			client.setPlayerData(apiResult.getUpdatedPlayerData());
			client.setAuthToken(apiResult.getChangedAuthToken());
			
		}
		
		// Shows the being available list
		client.sendMessage(
				ClientHelper.returnFormattedPlayerData(client, playerData, client.getActiveBeingCode())
				);
		
	}
	
	/**
	 * Returns information about the player logged (and active being, if it exists)
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleWhoAmICommand(ClientConnection client) throws ClientException {
		
		Player playerData = 
				client.getPlayerData()
					.orElseThrow(()-> new ClientException(LocalizedMessages.COMMAND_ONLY_LOGGED ));
		
		Optional<Being> activeBeing = client.getActiveBeing();

		// Shows up player information
		client.sendMessage(
				ClientHelper.returnFormattedPlayerData(client, playerData, client.getActiveBeingCode()));
		

		// If there's an active being
		if (activeBeing.isPresent()) {
			
			// Shows up being information			
			client.sendMessage(
					ClientHelper.returnFormattedBeingData(client, activeBeing.get()));
		}
	}
	
	/**
	 * Returns information about the current place of active being.
	 * 
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleWhereAmICommand(ClientConnection client) throws ClientException {
		
		Optional<Place> curPlace = client.getCurPlace();
		
		if (curPlace.isPresent()) {
			
			client.sendMessage(
					ClientHelper.returnFormattedPlaceData(client, curPlace.get()));
			
		} else {
			throw new ClientException(LocalizedMessages.COMMAND_NO_BEING);
		}
		
	}
	
	private void handleExitCommand(ClientConnection client) {
		
		if (client.isAdmin()) {
			client.setAdmin(false);
		} else if (client.hasBeingSelected()) {
			client.clearBeingInformation();
		} else if (client.isLogged()) {
			client.clearState();
		} else {
			
			try {
				client.sendFile(ClientHelper.GOODBYE_FILE);
			
			} catch(IOException e) {
				log.error("Error trying to send goodbye file", e);
			}
			
			connFactory.closeConnection(client.getConnection().getConnectionId());
		}
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
	public void handleCommand(ClientConnection client, CommandState command) throws ClientException {

		try {
		
			switch(command.getCommand().getCommandId()) {
			
				case SystemCommandHandler.REGISTER_COMMAND: 
					handleRegisterCommand(client, command);
					break;			
					
				case SystemCommandHandler.ACTIVATE_COMMAND: 
					handleActivateCommand(client, command);
					break;
				
					
				case SystemCommandHandler.PASSWORD_COMMAND:
					handlePasswordCommand(client, command);
					break;
				
				case SystemCommandHandler.QUIT_COMMAND: 
					
					try {
						client.sendFile(ClientHelper.GOODBYE_FILE);
					} catch(IOException e) {
						log.error("Error while trying to send goodbye file", e);
					}
					connFactory.closeConnection(client.getConnection().getConnectionId());
					break;
				case SystemCommandHandler.EXIT_COMMAND:
					handleExitCommand(client);
					break;
				case SystemCommandHandler.HELP_COMMAND: 
					handleHelpCommand(client);
					break;
				
				case SystemCommandHandler.LOGIN_COMMAND: 
					handleLoginCommand(client, command);
					break;
				
				case SystemCommandHandler.LOGOUT_COMMAND: 
					handleLogoutCommand(client);
					break;
				
				case SystemCommandHandler.CHANGEPROF_COMMAND: 
					handleChangeProfileCommand(client, command);
					break;
				
				case SystemCommandHandler.CREATEBEING_COMMAND: 
					handleCreateBeingCommand(client, command);
					break;
				
				case SystemCommandHandler.SELECTBEING_COMMAND: 
					handleSelectBeingCommand(client, command);
					break;
				
				case SystemCommandHandler.DELETEBEING_COMMAND: 
					handleDestroyBeingCommand(client, command);
					break;
				
				case SystemCommandHandler.WHOAMI_COMMAND: 
					handleWhoAmICommand(client);
					break;
				
				case SystemCommandHandler.WHEREAMI_COMMAND: 
					handleWhereAmICommand(client);
					break;
				
				default: 
					client.sendMessage(LocalizedMessages.COMMAND_NOT_SUPPORTED);
				
			}
		} finally {
			client.setCurCommandState(null);
		}
	}
}
