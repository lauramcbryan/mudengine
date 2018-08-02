package com.jpinfo.mudengine.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpinfo.mudengine.common.action.CommandParam;

import lombok.Getter;
import lombok.Setter;

public class CommandParamState {
	
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	private static final Pattern VALID_NUMBER_REGEX = 
		    Pattern.compile("^[0-9.%+-]$", Pattern.CASE_INSENSITIVE);
	

	@Getter
	private CommandParam parameter;
	
	@Getter
	private Object enteredValue;
	
	private boolean entered;
	
	@Setter
	private Map<String, Object> dynamicDomainValues;
	
	public CommandParamState(CommandParam parameter) {
		this.parameter = parameter;
		this.entered = false;
	}
	
	public String getName() {
		return this.parameter.getName();
	}
	
	public Map<String, Object> getDomainValues() {
		
		Map<String, Object> total = new HashMap<>();
		
		if (parameter.getStaticDomainValues()!=null)
			total.putAll(parameter.getStaticDomainValues());
		
		if (this.dynamicDomainValues!=null)
			total.putAll(this.dynamicDomainValues);
		
		return total;
		
	}

	public void setEnteredValue(String enteredValue) {
		
		if ((enteredValue!=null) && (!enteredValue.isEmpty())) {

			if (this.getDomainValues().containsKey(enteredValue)) {
				this.enteredValue = this.getDomainValues().get(enteredValue);
			} else {
					this.enteredValue = enteredValue;
			}			
		}
		
		
		this.entered = true;
	}
	
	public String getEffectiveValue() {
		return (enteredValue!=null ? enteredValue.toString(): parameter.getDefaultValue());
	}

	public boolean isValid() {
		
		Object sample = (enteredValue!=null ? enteredValue: parameter.getDefaultValue());
		
		return entered &&
				(sample!=null || !parameter.isRequired()) &&
				(this.getDomainValues().isEmpty() || this.getDomainValues().containsValue(sample)) &&
				(parameter.getType() != CommandParam.enumParamTypes.ANY_NUMBER || checkNumberFormat(sample)) &&
				(parameter.getType() != CommandParam.enumParamTypes.EMAIL || checkEmailFormat(sample))
				;
	}
	
	
	private boolean checkEmailFormat(Object email) {
		
		if (email!=null) {
			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email.toString());
	        return matcher.find();
		}
		
		return false;
	}	
	
	private boolean checkNumberFormat(Object number) {
		
		if (number!=null) {
			Matcher matcher = VALID_NUMBER_REGEX .matcher(number.toString());
	        return matcher.find();
		}
		
		return false;
	}
	
	public String getInputMessage() {
		
		StringBuilder msg = new StringBuilder();
		
		msg.append(parameter.getInputMessage());
		
		if (!this.getDomainValues().isEmpty()) {
			
			boolean first = true;
			msg.append(" [");
			for(String curDomainValue: this.getDomainValues().keySet()) {
				
				if (!first) msg.append(", ");
				
				msg.append(curDomainValue);
				
				first = false;
			}
			msg.append("]");
		}
		
		if (parameter.getDefaultValue()!=null) {
			msg.append(" <ENTER = ").append(parameter.getDefaultValue()).append(">");
		}
		
		msg.append(": ");
		
		return msg.toString();

	}
	
}
