package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import com.jpinfo.mudengine.world.model.pk.TerrainCategorySkillPK;

@Entity
@Table(name="MUD_TERRAIN_CATG_SKILL")
public class TerrainCategorySkill {
	
	@EmbeddedId
	private TerrainCategorySkillPK pk;
	
	@Column(name="SKILL_OFFSET")
	private Float offset;

	

	public TerrainCategorySkillPK getPk() {
		return pk;
	}

	public void setPk(TerrainCategorySkillPK pk) {
		this.pk = pk;
	}

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}
	
}
