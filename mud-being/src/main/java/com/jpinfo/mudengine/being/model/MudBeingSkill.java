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
	private Integer skillValue;

	public MudBeingSkill() {
	}

	public MudBeingSkillPK getId() {
		return this.id;
	}

	public void setId(MudBeingSkillPK id) {
		this.id = id;
	}

	public Integer getValue() {
		return this.skillValue;
	}

	public void setValue(Integer skillOffset) {
		this.skillValue = skillOffset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MudBeingSkill other = (MudBeingSkill) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}