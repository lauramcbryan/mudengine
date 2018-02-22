package com.jpinfo.mudengine.message.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.message.model.pk.MudMessageParmPK;

@Entity
@Table(name="MUD_MESSAGE_PARM")
public class MudMessageParm {
	
	@EmbeddedId
	private MudMessageParmPK id;	

	@Column(name="VALUE")
	private String value;

	public MudMessageParmPK getId() {
		return id;
	}

	public void setId(MudMessageParmPK id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
