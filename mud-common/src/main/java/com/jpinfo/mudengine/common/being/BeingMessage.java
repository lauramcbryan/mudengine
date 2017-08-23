package com.jpinfo.mudengine.common.being;

import java.util.Date;

public class BeingMessage implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Date messageDateTime;
	
	private String message;
	
	private String sender;
	
	private Long senderCode;
	
	
	public BeingMessage() {
		
	}


	public Date getMessageDateTime() {
		return messageDateTime;
	}


	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public Long getSenderCode() {
		return senderCode;
	}


	public void setSenderCode(Long senderCode) {
		this.senderCode = senderCode;
	}
	
	

}
