package com.jpinfo.mudengine.action.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.action.model.pk.MudActionClassCommandParameterPK;

import lombok.Data;

@Entity
@Table(name="MUD_ACTION_CLASS_CMD_PARAMETER")
@Data
public class MudActionClassCommandParameter {

	@EmbeddedId
	private MudActionClassCommandParameterPK pk;

	@Column(name="input_message")
	private String inputMessage;
	
	private String type;
	
	@Column(name="required")
	private Integer required;
	
	private String domainValues;
	
	private String defaultValue;	
}
