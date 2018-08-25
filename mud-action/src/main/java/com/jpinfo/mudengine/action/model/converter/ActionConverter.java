package com.jpinfo.mudengine.action.model.converter;

import java.util.Optional;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassCommand;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;

public class ActionConverter {

	private ActionConverter() { }
	
	public static Action convert(MudAction dbAction) {
		
		Action state = new Action();
		
		state.setActionId(dbAction.getActionId());
		state.setStartTurn(dbAction.getStartTurn());
		state.setEndTurn(dbAction.getEndTurn());
		state.setCurState(Action.EnumActionState.values()[dbAction.getCurrState()]);
		state.setActionClassCode(dbAction.getActionClassCode());
		state.setActorCode(dbAction.getActorCode());
		
		state.setMediatorCode(dbAction.getMediatorCode());
		
		if (dbAction.getMediatorType()!=null)
			state.setMediatorType(EnumTargetType.valueOf(dbAction.getMediatorType()));
		
		state.setTargetCode(dbAction.getTargetCode());
		state.setTargetType(Action.EnumTargetType.valueOf(dbAction.getTargetType()));
		
		return state;
	}
	
	public static Action build(MudActionClass actionClass, MudActionClassCommand command, 
			Long actorCode, Optional<String> mediatorCode, String targetCode) {
		
		Action action = new Action();
		
		
		// Type of the action
		action.setActionClassCode(command.getActionClassCode());
		
		// Actor (and issuer that is the same at this release)
		action.setActorCode(actorCode);
		action.setIssuerCode(actorCode);

		
		// Mediator
		action.setMediatorType(EnumTargetType.valueOf(actionClass.getMediatorType()));
		
		if (mediatorCode.isPresent())
			action.setMediatorCode(mediatorCode.get());
		
		// Target
		action.setTargetType(EnumTargetType.valueOf(actionClass.getTargetType()));
		action.setTargetCode(targetCode);

		return action;
	}
}
