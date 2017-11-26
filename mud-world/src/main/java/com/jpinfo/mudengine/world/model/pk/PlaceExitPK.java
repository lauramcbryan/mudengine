package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PlaceExitPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer placeCode;
	
	@Column(length = 10)
	private String direction;

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
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
		PlaceExitPK other = (PlaceExitPK) obj;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (placeCode == null) {
			if (other.placeCode != null)
				return false;
		} else if (!placeCode.equals(other.placeCode))
			return false;
		return true;
	}

	
}
