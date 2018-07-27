package com.jpinfo.mudengine.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jpinfo.mudengine.common.action.Command;

public class CommandState {

	private Command command;
	
	private List<CommandParamState> paramStateList;
	
	public CommandState(Command c) {
		
		this.command = c;
		this.paramStateList = new ArrayList<>();
		
		if (c.getParameters()!=null) {
		
			c.getParameters().forEach(d -> {
				
				CommandParamState paramState = new CommandParamState(d);
				
				paramStateList.add(paramState);
			});
		}
	}

	public Command getCommand() {
		return command;
	}
	
	public List<CommandParamState> getParameters() {
		return this.paramStateList;
	}
	
	public Optional<CommandParamState> getNextParameter() {
		
		return paramStateList.stream()
				.filter(e-> !e.isValid())
				.findFirst();
	}
	
	public boolean isReady() {
		
		return paramStateList.stream()
				.noneMatch(d -> !d.isValid());
	}
}
