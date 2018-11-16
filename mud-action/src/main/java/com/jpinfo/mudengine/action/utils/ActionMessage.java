package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.message.MessageRequest;

import lombok.Data;

@Data
public class ActionMessage extends MessageRequest {
	
	private Long targetCode;
	private Action.EnumTargetType targetType;
	
	public ActionMessage() {
		this.targetType = Action.EnumTargetType.BEING;
	}

}
