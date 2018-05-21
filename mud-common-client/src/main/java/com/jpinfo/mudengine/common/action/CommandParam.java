package com.jpinfo.mudengine.common.action;

import java.util.Collections;
import java.util.List;


public class CommandParam {

	public static enum enumParamTypes { anyString, email, anyNumber};

	private String name;
	private String inputMessage;
	private enumParamTypes type;
	private boolean required;
	
	private List<String> domainValues;
	private String defaultValue;
	

	public CommandParam() {
		this.domainValues = Collections.emptyList();
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getInputMessage() {
		
		StringBuffer msg = new StringBuffer();
		
		msg.append(this.inputMessage);
		
		if (!domainValues.isEmpty()) {
			
			boolean first = true;
			msg.append(" [");
			for(String curDomainValue: domainValues) {
				
				if (!first) msg.append(", ");
				
				msg.append(curDomainValue);
				
				first = false;
			}
			msg.append("]");
		}
		
		if (defaultValue!=null) {
			msg.append(" <ENTER = ").append(defaultValue).append(">");
		}
		
		msg.append(": ");
		
		return msg.toString();

	}


	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}


	public enumParamTypes getType() {
		return type;
	}


	public void setType(enumParamTypes type) {
		this.type = type;
	}


	public boolean isRequired() {
		return required;
	}


	public void setRequired(boolean required) {
		this.required = required;
	}


	public List<String> getDomainValues() {
		return domainValues;
	}


	public void setDomainValues(List<String> domainValues) {
		this.domainValues = domainValues;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
