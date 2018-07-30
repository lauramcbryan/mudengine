package com.jpinfo.mudengine.client.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpinfo.mudengine.common.action.CommandParam;

public class CommandParamState {
	
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	private static final Pattern VALID_NUMBER_REGEX = 
		    Pattern.compile("^[0-9.%+-]$", Pattern.CASE_INSENSITIVE);
	

	private CommandParam parameter;
	
	private String enteredValue;
	
	private boolean entered;
	
	public CommandParamState(CommandParam parameter) {
		this.parameter = parameter;
		this.entered = false;
	}
	
	public String getName() {
		return this.parameter.getName();
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
				(parameter.getType() != CommandParam.enumParamTypes.ANY_NUMBER || checkNumberFormat(sample)) &&
				(parameter.getType() != CommandParam.enumParamTypes.EMAIL || checkEmailFormat(sample))
				;
	}
	
	
	private boolean checkEmailFormat(String email) {
		
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
	}	
	
	private boolean checkNumberFormat(String number) {
		
		Matcher matcher = VALID_NUMBER_REGEX .matcher(number);
        return matcher.find();
	}	
	
}
