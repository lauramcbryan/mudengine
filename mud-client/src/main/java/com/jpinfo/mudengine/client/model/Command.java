package com.jpinfo.mudengine.client.model;

import java.util.ArrayList;
import java.util.List;

public class Command {
	
	public static enum enumCategory {GAME, SYSTEM};
	
	private String verb;
	private enumCategory category;
	
	private List<CommandParam> parameters;
	
	public Command() {
		this.parameters = new ArrayList<CommandParam>();
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
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
		this.parameters = parameters;
	}
	
	public boolean isReady() {
		if (parameters!=null) {
			return parameters.stream().allMatch(d-> d.isValid());
		} else {
			return true;
		}
	}

}
