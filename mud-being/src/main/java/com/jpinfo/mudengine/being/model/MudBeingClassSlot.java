package com.jpinfo.mudengine.being.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingClassSlotPK;

@Entity(name="MUD_BEING_CLASS_SLOT")
public class MudBeingClassSlot {
	
	@EmbeddedId
	private MudBeingClassSlotPK id;
	
	public MudBeingClassSlot() {
		
	}

	public MudBeingClassSlotPK getId() {
		return id;
	}

	public void setId(MudBeingClassSlotPK id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MudBeingClassSlot other = (MudBeingClassSlot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
