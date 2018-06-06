package com.jpinfo.mudengine.common.action;

import java.util.ArrayList;
import java.util.List;

public class Command {

	public static enum enumCategory {GAME, SYSTEM};
	
	private Integer commandId;
	
	private String verb;
	private String description;
	private String usage;
	private boolean logged;
	
	private enumCategory category;
	
	private List<CommandParam> parameters;
	
	public Command() {
		this.parameters = new ArrayList<CommandParam>();
		this.logged = true;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public enumCategory getCategory() {
		return category;
	}

	public void setCategory(enumCategory category) {
		this.category = category;
	}
	
	public List<CommandParam> getParameters() {
		return parameters;
	}

	public void setParameters(List<CommandParam> parameters) {
		
		if (parameters!=null)
			this.parameters = parameters;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}
	
}
