package com.jpinfo.mudengine.client.model;

import java.util.List;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.common.action.Command;

public class VerbDictionary {

	private List<Command> dictionary;

	public List<Command> getDictionary() {
		return dictionary;
	}

	public void setDictionary(List<Command> dictionary) {
		this.dictionary = dictionary;
	}
	
	public CommandState getCommand(String enteredValue) throws ClientException {
		
		Command choosenCommand = 
			dictionary.stream()
				.filter(d-> enteredValue.startsWith(d.getVerb()))
				.findFirst()
				.orElseThrow(() -> new ClientException("Unknown command"));
		
		return new CommandState(choosenCommand);
	}
	 
}
