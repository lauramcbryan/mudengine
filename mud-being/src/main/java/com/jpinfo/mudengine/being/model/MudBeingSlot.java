package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name="MUD_BEING_SLOT")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudBeingSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingSlotPK id;

	private Long itemCode;	
}
