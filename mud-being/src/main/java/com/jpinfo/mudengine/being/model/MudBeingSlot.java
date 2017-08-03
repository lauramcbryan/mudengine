package com.jpinfo.mudengine.being.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.jpinfo.mudengine.being.model.pk.MudBeingSlotPK;

@Entity(name="MUD_BEING_SLOT")
public class MudBeingSlot {
	
	@EmbeddedId
	private MudBeingSlotPK id;

	private Long itemCode;
	
	public MudBeingSlot() {
		
	}

	public MudBeingSlotPK getId() {
		return id;
	}

	public void setId(MudBeingSlotPK id) {
		this.id = id;
	}

	public Long getItemCode() {
		return itemCode;
	}

	public void setItemCode(Long itemCode) {
		this.itemCode = itemCode;
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
		MudBeingSlot other = (MudBeingSlot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
