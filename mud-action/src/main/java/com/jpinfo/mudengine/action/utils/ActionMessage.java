package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;

public class ActionMessage {
	
	private Long senderCode;
	
	private Long targetCode;
	private Action.EnumTargetType targetType;
	
	public String messageKey;
	public String[] args;
	
	public String plainMessage;

	public ActionMessage(Long senderCode, Long targetCode, Action.EnumTargetType targetType, String messageKey, String... args) {

		this.senderCode = senderCode;
		this.targetCode = targetCode;
		this.targetType = targetType;
		
		this.messageKey = messageKey;
		this.args = args;
	}
	
	public ActionMessage(Long senderCode, Long targetCode, String targetType, String messageKey, String... args) {
		this(senderCode, targetCode, Action.EnumTargetType.valueOf(targetType), messageKey, args);
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
