package com.jpinfo.mudengine.being.model.pk;

import javax.persistence.Embeddable;

@Embeddable
public class MudBeingSlotPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long beingCode;
	
	private String slotCode;
	
	public MudBeingSlotPK() {
	}


	public Long getBeingCode() {
		return beingCode;
	}


	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}


	public String getSlotCode() {
		return slotCode;
	}


	public void setSlotCode(String slotCode) {
		this.slotCode = slotCode;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((slotCode == null) ? 0 : slotCode.hashCode());
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
		MudBeingSlotPK other = (MudBeingSlotPK) obj;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		if (slotCode == null) {
			if (other.slotCode != null)
				return false;
		} else if (!slotCode.equals(other.slotCode))
			return false;
		return true;
	}
}
