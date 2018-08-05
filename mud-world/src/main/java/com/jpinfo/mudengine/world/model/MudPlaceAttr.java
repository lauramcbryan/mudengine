package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import com.jpinfo.mudengine.world.model.pk.PlaceAttrPK;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="MUD_PLACE_ATTR")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudPlaceAttr {

	@EmbeddedId
	private PlaceAttrPK id;
	
	@Column(name="ATTR_VALUE", nullable = false)
	@ColumnDefault(value = "0")
	private Integer attrValue;
	
	@Transient
	public String getAttrCode() {
		return id.getAttrCode();
	}
}
