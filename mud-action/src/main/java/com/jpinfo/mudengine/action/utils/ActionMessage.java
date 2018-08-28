package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;

import lombok.Getter;

@Getter
public class ActionMessage {
	
	private Long senderCode;
	
	private Long targetCode;
	private Action.EnumTargetType targetType;
	
	private String messageKey;
	private String[] args;
	
	private boolean plainMessage;
	
	public ActionMessage() {
		this.targetType = Action.EnumTargetType.BEING;
	}

	private ActionMessage(Long senderCode, Long targetCode, boolean plainMessage, Action.EnumTargetType targetType, String messageKey, String... args) {

		this.senderCode = senderCode;
		this.targetCode = targetCode;
		this.targetType = targetType;
		
		this.messageKey = messageKey;
		this.plainMessage = plainMessage;
		this.args = args;
	}
	
	public ActionMessage(Long senderCode, Long targetCode, String targetType, String messageKey, String... args) {
		this(senderCode, targetCode, false, Action.EnumTargetType.valueOf(targetType), messageKey, args);
	}

	public ActionMessage(Long senderCode, Long targetCode, String targetType, String plainMessage) {
		this(senderCode, targetCode, true, Action.EnumTargetType.valueOf(targetType), plainMessage);
	}	
}
