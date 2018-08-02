package com.jpinfo.mudengine.client.interfaces;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.model.ClientConnection;
import com.jpinfo.mudengine.client.model.CommandState;
import com.jpinfo.mudengine.common.action.Command;

public interface CommandHandler {

	CommandState initializeCommand(ClientConnection client, Command command);

	void handleCommand(ClientConnection client, CommandState command) throws ClientException;

}