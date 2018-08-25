package com.jpinfo.mudengine.action.model.converter;

import com.jpinfo.mudengine.action.model.MudActionClassPrereq;
import com.jpinfo.mudengine.common.action.ActionClassPrereq;

public class ActionClassPrereqConverter {

	private ActionClassPrereqConverter() { }
	
	public static ActionClassPrereq convert(MudActionClassPrereq curPrereq) {

		ActionClassPrereq newPrereq = new ActionClassPrereq();
		
		newPrereq.setEvalOrder(curPrereq.getEvalOrder());
		newPrereq.setCheckExpression(curPrereq.getCheckExpression());
		newPrereq.setFailExpression(curPrereq.getFailExpression());
		
		return newPrereq;
	}
}
