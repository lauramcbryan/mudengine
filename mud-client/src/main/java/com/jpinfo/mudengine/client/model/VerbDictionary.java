package com.jpinfo.mudengine.client.model;

import java.util.List;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.utils.ClientLocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;

public class VerbDictionary {

	private List<Command> commandList;

	public List<Command> getCommandList() {
		return commandList;
	}

	public void setCommandList(List<Command> dictionary) {
		this.commandList = dictionary;
	}
	
	public Command getCommand(String enteredValue) throws ClientException {
		
		return 
			commandList.stream()
				.filter(d-> enteredValue.startsWith(d.getVerb()))
				.findFirst()
				.orElseThrow(() -> new ClientException(ClientLocalizedMessages.COMMAND_UNKNOWN));
	}
	 
}
