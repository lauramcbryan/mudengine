package com.jpinfo.mudengine.message.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.message.model.pk.MudMessageEntityPK;

import lombok.Data;

@Entity
@Table(name="MUD_MESSAGE_ENTITY")
@Data
public class MudMessageEntity {

	@EmbeddedId
	private MudMessageEntityPK id;
}
