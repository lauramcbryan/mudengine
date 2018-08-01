package com.jpinfo.mudengine.client.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jpinfo.mudengine.client.exception.ClientException;
import com.jpinfo.mudengine.common.action.Command;

public class CommandState {

	private Command command;
	
	private List<CommandParamState> paramStateList;
	
	public CommandState(Command c, List<CommandParamState> paramStateList) {
		
		this.command = c;
		this.paramStateList = paramStateList;
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
	
	
	/**
	 * Get the value entered for a command parameter.
	 * 
	 * @param command
	 * @param key
	 * @param returnClass
	 * @return
	 * @throws ClientException
	 */
	public <T> T getParamValue(String key, Class<T> returnClass) throws ClientException  {
		
		CommandParamState foundParam = this.paramStateList.stream()
				.filter(d -> d.getParameter().getName().equals(key))
				.findFirst()
				.orElseThrow(() -> new ClientException("Parameter unknown"));
		
		
		if (foundParam.getEffectiveValue()!=null) {
			
			try {
				return returnClass.getConstructor(String.class).newInstance(foundParam.getEffectiveValue());
			} catch (Exception e) {
				
				throw new ClientException("System error retrieving parameter values.");
			}
		}
		
		
		return null;
	}
	
	public String getParamValue(String key) throws ClientException  {
		return getParamValue(key, String.class);
	}
	
	public Map<String, Object> toParamMap() {
		
		return this.paramStateList.stream()
				.collect(
						Collectors.toMap(
								CommandParamState::getName, 
								CommandParamState::getEffectiveValue)
				);
	}
}
