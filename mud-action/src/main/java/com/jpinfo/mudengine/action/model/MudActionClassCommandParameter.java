package com.jpinfo.mudengine.action.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.action.model.pk.MudActionClassCommandParameterPK;

@Entity
@Table(name="MUD_ACTION_CLASS_CMD_PARAMETER")
public class MudActionClassCommandParameter {

	@EmbeddedId
	private MudActionClassCommandParameterPK pk;

	@Column(name="input_message")
	private String inputMessage;
	
	private String type;
	
	@Column(name="required")
	private boolean required;
	
	private String domainValues;
	private String defaultValue;
	
	
	public MudActionClassCommandParameter() {
		
	}


	public MudActionClassCommandParameterPK getPk() {
		return pk;
	}


	public void setPk(MudActionClassCommandParameterPK pk) {
		this.pk = pk;
	}


	public String getInputMessage() {
		return inputMessage;
	}


	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isRequired() {
		return required;
	}


	public void setRequired(boolean required) {
		this.required = required;
	}


	public String getDomainValues() {
		return domainValues;
	}


	public void setDomainValues(String domainValues) {
		this.domainValues = domainValues;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
