package com.jpinfo.mudengine.action.exception;

public class ActionRefusedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public static final Integer GENERIC_ERROR = 999;
	
	private Integer messageCode;
	
	public ActionRefusedException(Integer messageCode) {
		super();
		this.messageCode = messageCode;
	}

	public Integer getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(Integer messageCode) {
		this.messageCode = messageCode;
	}

}
