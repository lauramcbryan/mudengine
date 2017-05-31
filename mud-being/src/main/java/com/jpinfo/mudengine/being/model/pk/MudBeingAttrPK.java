package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The primary key class for the mud_being_attr database table.
 * 
 */
@Embeddable
public class MudBeingAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Long beingCode;
	
	@Column(name="attr_code")
	private String attrCode;
	

	public MudBeingAttrPK() {
	}
	public Long getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}
	public String getAttrCode() {
		return attrCode;
	}
	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrCode == null) ? 0 : attrCode.hashCode());
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
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
		MudBeingAttrPK other = (MudBeingAttrPK) obj;
		if (attrCode == null) {
			if (other.attrCode != null)
				return false;
		} else if (!attrCode.equals(other.attrCode))
			return false;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		return true;
	}

}