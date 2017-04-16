package com.jpinfo.mudengine.beingClass.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.beingClass.model.pk.BeingClassSkillPK;


/**
 * The persistent class for the mud_being_class_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_class_skill")
public class MudBeingClassSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingClassSkillPK id;

	@Column(name="skill_value")
	private Integer skillValue;

	public MudBeingClassSkill() {
	}

	public BeingClassSkillPK getId() {
		return this.id;
	}

	public void setId(BeingClassSkillPK id) {
		this.id = id;
	}

	public Integer getSkillValue() {
		return this.skillValue;
	}

	public void setSkillValue(Integer skillValue) {
		this.skillValue = skillValue;
	}

}