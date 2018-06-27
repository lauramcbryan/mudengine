package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.PlaceClassAttrPK;

import lombok.Data;

@Entity
@Table(name="MUD_PLACE_CLASS_ATTR")
@Data
public class MudPlaceClassAttr {

	@EmbeddedId
	private PlaceClassAttrPK id;
	
	@Column(name="ATTR_VALUE", nullable = false)
	@ColumnDefault(value="0")
	private Integer attrValue;
}
