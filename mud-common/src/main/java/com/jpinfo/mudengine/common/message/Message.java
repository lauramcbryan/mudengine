package com.jpinfo.mudengine.common.message;

public class Message {

	private Long insertTurn;
	
	private Long senderCode;
	
	private String senderName;
	
	private String message;

	public Long getInsertTurn() {
		return insertTurn;
	}

	public void setInsertTurn(Long insertTurn) {
		this.insertTurn = insertTurn;
	}

	public Long getSenderCode() {
		return senderCode;
	}

	public void setSenderCode(Long senderCode) {
		this.senderCode = senderCode;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
