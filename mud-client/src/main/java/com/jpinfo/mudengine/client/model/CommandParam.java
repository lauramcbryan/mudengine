package com.jpinfo.mudengine.client.model;

import java.util.List;

public class CommandParam {
	
	public static enum enumParamTypes { anyString, email, anyNumber, currency };

	private String name;
	private String inputMessage;
	private enumParamTypes type;
	private boolean required;
	
	private List<String> domainValues;
	private String defaultValue;
	
	private String enteredValue;
	

	public CommandParam() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInputMessage() {
		return inputMessage;
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

	public String getEnteredValue() {
		return enteredValue;
	}

	public void setEnteredValue(String enteredValue) {
		this.enteredValue = enteredValue;
	}
	
	public String getEffectiveValue() {
		return (enteredValue!=null ? enteredValue:defaultValue);
	}
	
	public boolean isValid() {
		return 
				(enteredValue!=null || !this.required) &&
				(domainValues==null || domainValues.contains(enteredValue)) &&
				(this.type != enumParamTypes.anyNumber || checkNumberFormat(enteredValue)) &&
				(this.type != enumParamTypes.currency || checkCurrencyFormat(enteredValue)) &&
				(this.type != enumParamTypes.email || checkEmailFormat(enteredValue))
				;
	}
	
	
	private boolean checkEmailFormat(String email) {
		// TODO Check email format
		return true;
	}
	
	private boolean checkCurrencyFormat(String currency) {
		// TODO Check currency format
		return true;
	}
	
	private boolean checkNumberFormat(String number) {
		// TODO Check number format
		return true;
	}

}
