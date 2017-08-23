package com.jpinfo.mudengine.being.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.being.model.pk.MudBeingMessagePK;

@Entity
@Table(name="MUD_BEING_MESSAGE")
public class MudBeingMessage {
	
	@EmbeddedId
	private MudBeingMessagePK id;
	
	@Column(name="message")
	private String message;
	
	@Column(name="read_flag")
	private Boolean readFlag;
	
	@Column(name="sender_code")
	private Long senderCode;
	
	public MudBeingMessage() {
		
	}
	
	public MudBeingMessagePK getId() {
		return id;
	}

	public void setId(MudBeingMessagePK id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(Boolean readFlag) {
		this.readFlag = readFlag;
	}

	public Long getSenderCode() {
		return senderCode;
	}

	public void setSenderCode(Long senderCode) {
		this.senderCode = senderCode;
	}
}
