package com.jpinfo.mudengine.world.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpinfo.mudengine.world.model.pk.TerrainCategoryAttrPK;

@Entity
@Table(name="MUD_TERRAIN_CATG_ATTR")
public class TerrainCategoryAttr {

	@EmbeddedId
	private TerrainCategoryAttrPK pk;
	
	@Column(name="ATTR_OFFSET")
	private Float offset;

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}
}
