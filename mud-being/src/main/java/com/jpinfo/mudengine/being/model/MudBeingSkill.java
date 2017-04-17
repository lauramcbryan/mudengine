package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;


/**
 * The persistent class for the mud_being_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_skill")
public class MudBeingSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingSkillPK id;

	@Column(name="skill_value")
	private float skillValue;

	public MudBeingSkill() {
	}

	public MudBeingSkillPK getId() {
		return this.id;
	}

	public void setId(MudBeingSkillPK id) {
		this.id = id;
	}

	public float getValue() {
		return this.skillValue;
	}

	public void setValue(float skillOffset) {
		this.skillValue = skillOffset;
	}
}