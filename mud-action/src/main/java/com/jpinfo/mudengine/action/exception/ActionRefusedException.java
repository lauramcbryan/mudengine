package com.jpinfo.mudengine.action.exception;

public class ActionRefusedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
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
