package com.jpinfo.mudengine.message.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.message.model.pk.MudMessageParmPK;

import lombok.Data;

@Entity
@Table(name="MUD_MESSAGE_PARM")
@Data
public class MudMessageParm {
	
	@EmbeddedId
	private MudMessageParmPK id;	

	@Column(name="VALUE")
	private String value;
}
