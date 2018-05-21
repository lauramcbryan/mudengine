package com.jpinfo.mudengine.client.model;

import com.jpinfo.mudengine.common.action.CommandParam;

public class CommandParamState {

	private CommandParam parameter;
	
	private String enteredValue;
	
	private boolean entered;
	
	public CommandParamState(CommandParam parameter) {
		this.parameter = parameter;
		this.entered = false;
	}
	
	public CommandParam getParameter() {
		return this.parameter;
	}

	public String getEnteredValue() {
		return enteredValue;
	}

	public void setEnteredValue(String enteredValue) {
		this.enteredValue = enteredValue;
		this.entered = true;
	}
	
	public String getEffectiveValue() {
		return (enteredValue!=null && !enteredValue.isEmpty() ? enteredValue: parameter.getDefaultValue());
	}

	public boolean isValid() {
		
		String sample = getEffectiveValue();
		
		return entered &&
				(sample!=null || !parameter.isRequired()) &&
				(parameter.getDomainValues().isEmpty() || parameter.getDomainValues().contains(sample)) &&
				(parameter.getType() != CommandParam.enumParamTypes.anyNumber || checkNumberFormat(sample)) &&
				(parameter.getType() != CommandParam.enumParamTypes.email || checkEmailFormat(sample))
				;
	}
	
	
	private boolean checkEmailFormat(String email) {
		// TODO Check email format
		return true;
	}	
	
	private boolean checkNumberFormat(String number) {
		// TODO Check number format
		return true;
	}	
	
}
