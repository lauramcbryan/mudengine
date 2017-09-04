package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;

public class ActionMessages {
	
	private Long senderCode;
	
	private Long targetCode;
	private Action.EnumTargetType targetType;
	
	public String messageKey;
	public Object[] args;
	
	public String plainMessage;

	public ActionMessages(Long targetCode, Action.EnumTargetType targetType, String messageKey, Object... args) {

		this.targetCode = targetCode;
		this.targetType = targetType;
		this.args = args;
	}
	
	public ActionMessages(Long targetCode, String targetType, String messageKey, Object... args) {

		this.targetCode = targetCode;
		this.targetType = Action.EnumTargetType.valueOf(targetType);
		this.args = args;
	}
	
	public ActionMessages(Long senderCode, Long beingCode, String message) {

		this.senderCode = senderCode;
		this.targetCode = beingCode;
		this.targetType = Action.EnumTargetType.BEING;
		this.plainMessage = message;
	}

	public Long getSenderCode() {
		return senderCode;
	}

	public Long getTargetCode() {
		return targetCode;
	}

	public Action.EnumTargetType getTargetType() {
		return targetType;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public Object[] getArgs() {
		return args;
	}

	public String getPlainMessage() {
		return plainMessage;
	}
	
	
}
