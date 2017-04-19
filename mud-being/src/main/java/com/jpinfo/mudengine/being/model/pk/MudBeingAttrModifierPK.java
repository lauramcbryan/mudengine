package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The primary key class for the mud_being_attr database table.
 * 
 */
@Embeddable
public class MudBeingAttrModifierPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code")
	private Long beingCode;
	
	@Column(name="attr_code")
	private String attrCode;
	
	@Column(name="origin_code")
	private String originCode;

	@Column(name="origin_type")
	private String originType;

	public MudBeingAttrModifierPK() {
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
	public String getOriginCode() {
		return originCode;
	}
	public void setOriginCode(String originCode) {
		this.originCode = originCode;
	}
	public String getOriginType() {
		return originType;
	}
	public void setOriginType(String originType) {
		this.originType = originType;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrCode == null) ? 0 : attrCode.hashCode());
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((originCode == null) ? 0 : originCode.hashCode());
		result = prime * result + ((originType == null) ? 0 : originType.hashCode());
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
		MudBeingAttrModifierPK other = (MudBeingAttrModifierPK) obj;
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
		if (originCode == null) {
			if (other.originCode != null)
				return false;
		} else if (!originCode.equals(other.originCode))
			return false;
		if (originType == null) {
			if (other.originType != null)
				return false;
		} else if (!originType.equals(other.originType))
			return false;
		return true;
	}

}