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
	
}
