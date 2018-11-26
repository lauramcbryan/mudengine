package com.jpinfo.mudengine.common.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum enumCategory {GAME, SYSTEM, ADMIN}
	
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
