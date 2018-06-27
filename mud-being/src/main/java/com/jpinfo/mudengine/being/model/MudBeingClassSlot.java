package com.jpinfo.mudengine.being.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingClassSlotPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name="MUD_BEING_CLASS_SLOT")
@Data
@EqualsAndHashCode
public class MudBeingClassSlot {
	
	@EmbeddedId
	private MudBeingClassSlotPK id;
}
