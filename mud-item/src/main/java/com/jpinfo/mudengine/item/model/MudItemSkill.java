package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.MudItemSkillPK;


/**
 * The persistent class for the mud_item_skill database table.
 * 
 */
@Entity
@Table(name="mud_item_skill")
public class MudItemSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudItemSkillPK id;

	@Column(name="skill_offset")
	private float skillOffset;

	public MudItemSkill() {
	}

	public MudItemSkillPK getId() {
		return this.id;
	}

	public void setId(MudItemSkillPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.skillOffset;
	}

	public void setOffset(float skillOffset) {
		this.skillOffset = skillOffset;
	}
}