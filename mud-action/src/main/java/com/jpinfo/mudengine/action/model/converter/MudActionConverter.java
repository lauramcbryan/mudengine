package com.jpinfo.mudengine.action.model.converter;

import java.util.Optional;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;

public class MudActionConverter {

	private MudActionConverter() { }
	
	public static MudAction build(MudActionClass actionClass, MudActionClassCommand command, 
			String worldName, Long actorCode, Optional<String> mediatorCode, String targetCode) {
		
		MudAction dbAction = new MudAction();

		dbAction.setWorldName(worldName);
		
		// Type of the action
		dbAction.setActionClassCode(command.getActionClassCode());
		
		// Actor (and issuer that is the same at this release)
		dbAction.setActorCode(actorCode);
		dbAction.setIssuerCode(actorCode);

		
		// Mediator
		dbAction.setMediatorType(actionClass.getMediatorType());
		
		if (mediatorCode.isPresent())
			dbAction.setMediatorCode(mediatorCode.get());
		
		// Target
		dbAction.setTargetType(actionClass.getTargetType());
		dbAction.setTargetCode(targetCode);

		return dbAction;
	}
}
