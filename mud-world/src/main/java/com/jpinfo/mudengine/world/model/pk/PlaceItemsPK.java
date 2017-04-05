package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;
import javax.persistence.*;


@Embeddable
public class PlaceItemsPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="PLACE_CODE")
	private Integer placeCode;
	
	@Column(name="ITEM_CODE")
	private Integer itemCode;

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer place) {
		this.placeCode = place;
	}

	public Integer getItemCode() {
		return itemCode;
	}

	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemCode == null) ? 0 : itemCode.hashCode());
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
		PlaceItemsPK other = (PlaceItemsPK) obj;
		if (itemCode == null) {
			if (other.itemCode != null)
				return false;
		} else if (!itemCode.equals(other.itemCode))
			return false;
		if (placeCode == null) {
			if (other.placeCode != null)
				return false;
		} else if (!placeCode.equals(other.placeCode))
			return false;
		return true;
	}
}
