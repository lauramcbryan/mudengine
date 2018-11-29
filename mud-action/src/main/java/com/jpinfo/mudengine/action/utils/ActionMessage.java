package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.message.MessageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ActionMessage extends MessageRequest {
	
	private static final long serialVersionUID = 1L;
	
	private Long targetCode;
	private Action.EnumTargetType targetType;
	
	public ActionMessage() {
		this.targetType = Action.EnumTargetType.BEING;
	}

}
