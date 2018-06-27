package com.jpinfo.mudengine.message.model;

import javax.persistence.*;

import com.jpinfo.mudengine.message.model.pk.MudMessageLocalePK;

import lombok.Data;

@Entity
@Table(name="MUD_MESSAGE_LOCALE")
@Data
public class MudMessageLocale {
	
	@EmbeddedId
	private MudMessageLocalePK pk;
	
	@Column(name="MESSAGE_TEXT")
	private String messageText;
}
