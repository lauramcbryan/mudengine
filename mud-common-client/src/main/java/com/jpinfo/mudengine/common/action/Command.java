package com.jpinfo.mudengine.common.action;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Command {

	public enum enumCategory {GAME, SYSTEM}
	
	private Integer commandId;
	
	private String verb;
	private String description;
	private String usage;
	private boolean logged;
	
	private enumCategory category;
	
	private List<CommandParam> parameters;
	
	public Command() {
		this.parameters = new ArrayList<>();
		this.logged = true;
	}
}
