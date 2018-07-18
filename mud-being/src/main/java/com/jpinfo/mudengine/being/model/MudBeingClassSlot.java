package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingClassSlotPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name="MUD_BEING_CLASS_SLOT")
@Data
@EqualsAndHashCode
public class MudBeingClassSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private MudBeingClassSlotPK id;
}
