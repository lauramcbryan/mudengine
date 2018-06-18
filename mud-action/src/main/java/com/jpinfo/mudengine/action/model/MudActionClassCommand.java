package com.jpinfo.mudengine.action.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="MUD_ACTION_CLASS_CMD")
public class MudActionClassCommand {

	@Id
	@Column(name="COMMAND_ID")
	private Integer commandId;

	@Column(name="ACTION_CLASS_CODE")
	private Integer actionClassCode;
	
	@Column(name="MEDIATOR_TYPE")
	private String mediatorType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}
	
	@Column(name="TARGET_TYPE")
	private String targetType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}	
	
	private String verb;
	
	private String description;
	
	private String usage;
	
	
	private String locale;
	
	
	@OneToMany(mappedBy="pk.commandId", orphanRemoval=true)
	private Set<MudActionClassCommandParameter> parameterList;
	
	
	public MudActionClassCommand() {
		
	}

	public Integer getCommandId() {
		return commandId;
	}


	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}



	public Integer getActionClassCode() {
		return actionClassCode;
	}



	public void setActionClassCode(Integer actionClassCode) {
		this.actionClassCode = actionClassCode;
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Set<MudActionClassCommandParameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(Set<MudActionClassCommandParameter> parameterList) {
		this.parameterList = parameterList;
	}

	public String getMediatorType() {
		return mediatorType;
	}

	public void setMediatorType(String mediatorType) {
		this.mediatorType = mediatorType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
}
