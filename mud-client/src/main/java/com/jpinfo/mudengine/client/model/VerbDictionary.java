package com.jpinfo.mudengine.client.model;

import java.util.List;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.client.utils.LocalizedMessages;
import com.jpinfo.mudengine.common.action.Command;

public class VerbDictionary {

	private List<Command> commandList;

	public List<Command> getCommandList() {
		return commandList;
	}

	public void setCommandList(List<Command> dictionary) {
		this.commandList = dictionary;
	}
	
	public CommandState getCommand(String enteredValue) throws ClientException {
		
		Command choosenCommand = 
			commandList.stream()
				.filter(d-> enteredValue.startsWith(d.getVerb()))
				.findFirst()
				.orElseThrow(() -> new ClientException(LocalizedMessages.COMMAND_UNKNOWN));
		
		return new CommandState(choosenCommand);
	}
	 
}
