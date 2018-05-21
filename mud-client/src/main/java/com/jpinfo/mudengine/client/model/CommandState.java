package com.jpinfo.mudengine.client.model;

import java.util.List;
import java.util.Optional;

import com.jpinfo.mudengine.common.action.Command;

public class CommandState {

	private Command command;
	
	private List<CommandParamState> paramStateList;
	
	public CommandState(Command c) {
		
		this.command = c;
		
		c.getParameters().forEach(d -> {
			
			CommandParamState paramState = new CommandParamState(d);
			
			paramStateList.add(paramState);
		});
	}

	public Command getCommand() {
		return command;
	}
	
	public List<CommandParamState> getParameters() {
		return this.paramStateList;
	}
	
	public Optional<CommandParamState> getNextParameter() {
		
		Optional<CommandParamState> nextParam = 
				paramStateList.stream()
				.filter(e-> !e.isValid())
				.findFirst();

		return nextParam;
	}
	
	public boolean isReady() {
		
		return paramStateList.stream()
				.noneMatch(d -> !d.isValid());
	}
}
