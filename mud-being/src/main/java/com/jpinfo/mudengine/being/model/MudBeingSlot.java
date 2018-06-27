package com.jpinfo.mudengine.being.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name="MUD_BEING_SLOT")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudBeingSlot {
	
	@EmbeddedId
	private MudBeingSlotPK id;

	private Long itemCode;	
}
