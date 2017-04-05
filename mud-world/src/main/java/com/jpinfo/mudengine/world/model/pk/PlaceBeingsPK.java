package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;


import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PlaceBeingsPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="PLACE_CODE")
	private Integer placeCode;

	@Column(name="BEING_CODE")
	private Integer beingCode;

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlace(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public Integer getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((placeCode == null) ? 0 : placeCode.hashCode());
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
		PlaceBeingsPK other = (PlaceBeingsPK) obj;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		if (placeCode == null) {
			if (other.placeCode != null)
				return false;
		} else if (!placeCode.equals(other.placeCode))
			return false;
		return true;
	}

}
