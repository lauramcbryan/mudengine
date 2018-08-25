package com.jpinfo.mudengine.action.model.converter;

import java.util.Arrays;

import com.jpinfo.mudengine.action.model.MudActionClassCommandParameter;
import com.jpinfo.mudengine.common.action.CommandParam;

public class CommandParamConverter {

	private CommandParamConverter() { }
	
	public static CommandParam convert(MudActionClassCommandParameter dbParameter) {
		
		CommandParam newParam = new CommandParam();
		
		newParam.setName(dbParameter.getPk().getName());
		newParam.setRequired(((Integer)1).equals(dbParameter.getRequired()));
		newParam.setType(CommandParam.enumParamTypes.valueOf(dbParameter.getType()));
		newParam.setDefaultValue(dbParameter.getDefaultValue());
		
		if (dbParameter.getDomainValues()!=null) {
			
			newParam.setStaticDomainValues(
					Arrays.asList(dbParameter.getDomainValues().split(", "))
					);
		}
		
		newParam.setInputMessage(dbParameter.getInputMessage());
		
		return newParam;
	}
}
