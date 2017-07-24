package com.jpinfo.mudengine.being.model.pk;

import javax.persistence.Embeddable;

@Embeddable
public class MudBeingClassSlotPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String beingClassCode;
	
	private String slotCode;
	
	
	public MudBeingClassSlotPK() {
		
	}

	public String getBeingClassCode() {
		return beingClassCode;
	}


	public void setBeingClassCode(String beingClassCode) {
		this.beingClassCode = beingClassCode;
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
		result = prime * result + ((beingClassCode == null) ? 0 : beingClassCode.hashCode());
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
		MudBeingClassSlotPK other = (MudBeingClassSlotPK) obj;
		if (beingClassCode == null) {
			if (other.beingClassCode != null)
				return false;
		} else if (!beingClassCode.equals(other.beingClassCode))
			return false;
		if (slotCode == null) {
			if (other.slotCode != null)
				return false;
		} else if (!slotCode.equals(other.slotCode))
			return false;
		return true;
	}
}
