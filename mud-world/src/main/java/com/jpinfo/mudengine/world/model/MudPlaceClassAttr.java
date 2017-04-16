package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.world.model.pk.PlaceClassAttrPK;

@Entity
@Table(name="MUD_PLACE_CLASS_ATTR")
public class MudPlaceClassAttr {

	@EmbeddedId
	private PlaceClassAttrPK id;
	
	@Column(name="ATTR_OFFSET")
	private Float offset;

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}

	public PlaceClassAttrPK getId() {
		return id;
	}

	public void setId(PlaceClassAttrPK id) {
		this.id = id;
	}
	
}
