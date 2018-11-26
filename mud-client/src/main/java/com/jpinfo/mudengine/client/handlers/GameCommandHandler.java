package com.jpinfo.mudengine.client.handlers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandState;

@Component
@Qualifier("game-commands")
public class GameCommandHandler extends BaseCommandHandler {

	
	/**
	 * Main method for handling GAME commands.
	 * All these commands generates ACTIONs in game engine.
	 * The output of such ACTIONs can be received by the messages channel
	 * 
	 * @param client - object with player info
	 * @param command - command being processed
	 * @throws ClientException 
	 */
	public void handleCommand(ClientConnection client, CommandState command) throws ClientException {

		//String mediatorCode = command.getParamValue("mediatorCode");
		String targetCode = command.getParamValue("targetCode");
		Integer commandId = command.getCommand().getCommandId();
		
		
		// Perform the call to the API gateway
		api.insertCommand(client.getAuthToken(), commandId,
				Optional.empty(), targetCode);
	}
	
}
