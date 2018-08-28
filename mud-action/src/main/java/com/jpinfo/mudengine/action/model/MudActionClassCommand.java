package com.jpinfo.mudengine.action.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="MUD_ACTION_CLASS_CMD")
@Data
public class MudActionClassCommand {

	@Id
	@Column(name="COMMAND_ID")
	private Integer commandId;

	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
		
	private String verb;
	
	private String description;
	
	private String usage;
	
	private String locale;
	
	@Column(name="RUN_TYPE")
	private String runType;
	
	@OneToMany(mappedBy="pk.commandId", orphanRemoval=true)
	private Set<MudActionClassCommandParameter> parameterList;
	
}
