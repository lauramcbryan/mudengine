package com.jpinfo.mudengine.action.model.converter;

import java.util.stream.Collectors;

import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.common.action.Command;

public class CommandConverter {
	
	private CommandConverter() { }
	
	public static Command convert(MudActionClassCommand dbCommand) {
		
		Command result = new Command();
		
		result.setCommandId(dbCommand.getCommandId());
		result.setCategory(Command.enumCategory.GAME);
		result.setDescription(dbCommand.getDescription());
		result.setLogged(true);
		result.setUsage(dbCommand.getUsage());
		result.setVerb(dbCommand.getVerb());
		
		result.setParameters(
			dbCommand.getParameterList().stream()
				.map(CommandParamConverter::convert)
				.collect(Collectors.toList())
				);
		
		return result;
	}

}
