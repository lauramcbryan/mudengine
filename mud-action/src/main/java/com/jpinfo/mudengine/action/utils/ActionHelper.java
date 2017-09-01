package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.common.action.Action;

public class ActionHelper {
	
	public static Action buildAction(MudAction a) {
		
		Action state = new Action();
		
		state.setActionId(a.getActionId());
		state.setStartTurn(a.getStartTurn());
		state.setEndTurn(a.getEndTurn());
		state.setCurState(a.getCurrStateEnum());
		
		return state;
		
	}
}
