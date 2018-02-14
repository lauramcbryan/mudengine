package com.jpinfo.mudengine.message.model;

import javax.persistence.*;

import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;

@Entity
@Table(name="MUD_MESSAGE_LOCALE")
public class MudMessageLocale {
	
	@EmbeddedId
	private MudMessageLocalePK pk;
	
	@Column(name="MESSAGE_TEXT")
	private String messageText;

	public MudMessageLocalePK getPk() {
		return pk;
	}

	public void setPk(MudMessageLocalePK pk) {
		this.pk = pk;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
}
