package com.jpinfo.mudengine.action.model.converter;

import java.util.Optional;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.action.model.MudActionClassCommandParameter;

public class MudActionConverter {

	private MudActionConverter() { }
	
	public static MudAction build(MudActionClassCommand command, 
			String worldName, Long actorCode, Optional<String> mediatorCode, String targetCode) {
		
		MudAction dbAction = new MudAction();

		dbAction.setWorldName(worldName);
		dbAction.setRunType(command.getRunType());
		
		// Type of the action
		dbAction.setActionClassCode(command.getActionClassCode());
		
		// Actor (and issuer that is the same at this release)
		dbAction.setActorCode(actorCode);
		dbAction.setIssuerCode(actorCode);
		

		
		// Mediator
		Optional<MudActionClassCommandParameter> mediatorParam = 
			command.getParameterList().stream()
				.filter(d -> d.getPk().getName().equals("mediatorCode"))
				.findFirst();
		
		if (mediatorParam.isPresent())
			dbAction.setMediatorType(mediatorParam.get().getType());
		
		if (mediatorCode.isPresent())
			dbAction.setMediatorCode(mediatorCode.get());
		
		// Target
		Optional<MudActionClassCommandParameter> targetParam = 
				command.getParameterList().stream()
					.filter(d -> d.getPk().getName().equals("targetCode"))
					.findFirst();
		
		if (targetParam.isPresent())
			dbAction.setTargetType(targetParam.get().getType());

		dbAction.setTargetCode(targetCode);
		
		return dbAction;
	}
}
