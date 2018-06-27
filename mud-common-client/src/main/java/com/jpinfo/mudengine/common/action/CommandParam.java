package com.jpinfo.mudengine.common.action;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
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
}
