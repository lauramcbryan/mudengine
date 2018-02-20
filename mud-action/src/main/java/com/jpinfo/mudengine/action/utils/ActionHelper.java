package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.action.ActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;

public class ActionHelper {
	
	public static Action buildAction(MudAction a) {
		
		Action state = new Action();
		
		state.setActionId(a.getActionId());
		state.setStartTurn(a.getStartTurn());
		state.setEndTurn(a.getEndTurn());
		state.setCurState(a.getCurrStateEnum());
		state.setActionClassCode(a.getActionClassCode());
		state.setActorCode(a.getActorCode());
		
		state.setMediatorCode(a.getMediatorCode());
		state.setMediatorType(EnumTargetType.valueOf(a.getMediatorType()));
		
		state.setTargetCode(a.getTargetCode());
		state.setTargetType(a.getTargetTypeEnum());
		
		return state;
	}
	
	public static ActionClass buildActionClass(MudActionClass a) {
	
		ActionClass result = new ActionClass();
		
		result.setActionClassCode(a.getActionClassCode());
		result.setActionType(a.getActionType());
		result.setNroTurnsExpr(a.getNroTurnsExpr());
		result.setSuccessRateExpr(a.getSuccessRateExpr());
		result.setVerb(a.getVerb());
		
		for(MudActionClassPrereq curPrereq: a.getPrereqList()) {
			
			ActionClassPrereq newPrereq = new ActionClassPrereq();
			
			newPrereq.setEvalOrder(curPrereq.getEvalOrder());
			newPrereq.setCheckExpression(curPrereq.getCheckExpression());
			newPrereq.setFailExpression(curPrereq.getFailExpression());
			
			result.getPrereqList().add(newPrereq);
		}

		for(MudActionClassEffect curEffect: a.getEffectList()) {
			
			ActionClassEffect newEffect = new ActionClassEffect();
			
			newEffect.setEvalOrder(curEffect.getEvalOrder());
			newEffect.setExpression(curEffect.getExpression());
			newEffect.setMessageExpression(curEffect.getMessageExpression());
			
			result.getEffectList().add(newEffect);
		}
		
		return result;
	}
}
