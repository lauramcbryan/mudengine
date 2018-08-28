package com.jpinfo.mudengine.action.model.converter;


import com.jpinfo.mudengine.action.model.MudAction;
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
		state.setRunType(Action.EnumRunningType.valueOf(dbAction.getRunType()));
		
		state.setMediatorCode(dbAction.getMediatorCode());
		
		if (dbAction.getMediatorType()!=null)
			state.setMediatorType(EnumTargetType.valueOf(dbAction.getMediatorType()));
		
		state.setTargetCode(dbAction.getTargetCode());
		state.setTargetType(Action.EnumTargetType.valueOf(dbAction.getTargetType()));
		
		return state;
	}
}
