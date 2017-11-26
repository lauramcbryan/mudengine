package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.PlaceClassAttrPK;

@Entity
@Table(name="MUD_PLACE_CLASS_ATTR")
public class MudPlaceClassAttr {

	@EmbeddedId
	private PlaceClassAttrPK id;
	
	@Column(name="ATTR_VALUE", nullable = false)
	@ColumnDefault(value="0")
	private Integer attrValue;

	

	public Integer getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(Integer attrValue) {
		this.attrValue = attrValue;
	}

	public PlaceClassAttrPK getId() {
		return id;
	}

	public void setId(PlaceClassAttrPK id) {
		this.id = id;
	}
	
}
