package com.jpinfo.mudengine.action.model.converter;

import com.jpinfo.mudengine.action.model.MudActionClassEffect;
import com.jpinfo.mudengine.common.action.ActionClassEffect;

public class ActionClassEffectConverter {

	private ActionClassEffectConverter() { }
	
	public static ActionClassEffect convert(MudActionClassEffect dbEffect) {
		
		ActionClassEffect newEffect = new ActionClassEffect();
		
		newEffect.setEvalOrder(dbEffect.getEvalOrder());
		newEffect.setExpression(dbEffect.getExpression());
		newEffect.setMessageExpression(dbEffect.getMessageExpression());
		
		return newEffect;
	}
}
