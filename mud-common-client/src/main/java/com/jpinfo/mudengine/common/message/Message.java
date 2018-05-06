package com.jpinfo.mudengine.common.message;

public class Message {

	private String messageDate;
	
	private Long senderCode;
	
	private String senderName;
	
	private String message;

	public String getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
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
