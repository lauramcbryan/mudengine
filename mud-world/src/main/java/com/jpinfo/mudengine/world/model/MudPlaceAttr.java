package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.world.model.pk.PlaceAttrPK;

@Entity
@Table(name="MUD_PLACE_ATTR")
public class MudPlaceAttr {

	@EmbeddedId
	private PlaceAttrPK id;
	
	@Column(name="ATTR_VALUE")
	private Integer attrValue;

	

	public Integer getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(Integer attrValue) {
		this.attrValue = attrValue;
	}

	public PlaceAttrPK getId() {
		return id;
	}

	public void setId(PlaceAttrPK id) {
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
		MudPlaceAttr other = (MudPlaceAttr) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
