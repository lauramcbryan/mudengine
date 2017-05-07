package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionSimpleState;

public class ActionHelper {
	
	public static ActionSimpleState buildSimpleState(MudAction a) {
		
		ActionSimpleState state = new ActionSimpleState();
		
		state.setActionId(a.getActionId());
		state.setStartTurn(a.getStartTurn());
		state.setEndTurn(a.getEndTurn());
		state.setCurState(a.getCurrState());
		
		return state;
		
	}
	
	public static MudAction buildMudAction(Action requestAction) {
		
		MudAction mudAction = new MudAction();
		
		mudAction.setActorCode(requestAction.getActorCode());
		mudAction.setIssuerCode(requestAction.getIssuerCode());
		
		mudAction.setActionCode(requestAction.getActionCode());
		mudAction.setMediatorCode(requestAction.getMediatorCode());
		mudAction.setPlaceCode(requestAction.getPlaceCode());
		mudAction.setTargetCode(requestAction.getTargetCode());
		mudAction.setTargetType(requestAction.getTargetType());
		mudAction.setWorldName(requestAction.getWorldName());
		
		return mudAction;
	}
	
	
	private static String getPlaceServiceUrl() {
		return "http://localhost:8080/place/{id}";
	}

	private static String getPlaceClassServiceUrl() {
		return "http://localhost:8080/place/class/{id}";
	}
	
	private static String getItemServiceUrl() {
		return "http://localhost:8084/item/{id}";
	}

	private static String getBeingServiceUrl() {
		return "http://localhost:8088/being/{id}";
	}
	
}
