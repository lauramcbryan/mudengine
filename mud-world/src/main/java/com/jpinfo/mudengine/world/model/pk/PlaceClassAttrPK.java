package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PlaceClassAttrPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="PLACE_CLASS_CODE")
	private String placeClassCode;

	@Column(name="ATTR_CODE")
	private String attrCode;

	public String getPlaceClassCode() {
		return placeClassCode;
	}

	public void setPlaceClassCode(String placeClassCode) {
		this.placeClassCode = placeClassCode;
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
		result = prime * result + ((placeClassCode == null) ? 0 : placeClassCode.hashCode());
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
		PlaceClassAttrPK other = (PlaceClassAttrPK) obj;
		if (attrCode == null) {
			if (other.attrCode != null)
				return false;
		} else if (!attrCode.equals(other.attrCode))
			return false;
		if (placeClassCode == null) {
			if (other.placeClassCode != null)
				return false;
		} else if (!placeClassCode.equals(other.placeClassCode))
			return false;
		return true;
	}
	
	
	
}
