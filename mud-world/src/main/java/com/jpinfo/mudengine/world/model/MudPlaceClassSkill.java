package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import com.jpinfo.mudengine.world.model.pk.PlaceClassSkillPK;

@Entity
@Table(name="MUD_PLACE_CLASS_SKILL")
public class MudPlaceClassSkill {
	
	@EmbeddedId
	private PlaceClassSkillPK id;
	
	@Column(name="SKILL_OFFSET")
	private Float offset;

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}

	public PlaceClassSkillPK getId() {
		return id;
	}

	public void setId(PlaceClassSkillPK id) {
		this.id = id;
	}

	
}
