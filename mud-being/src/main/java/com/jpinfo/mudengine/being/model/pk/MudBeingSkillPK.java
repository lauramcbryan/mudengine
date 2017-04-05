package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.Skill;

/**
 * The primary key class for the mud_being_skills database table.
 * 
 */
@Embeddable
public class MudBeingSkillPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Integer beingCode;

	@ManyToOne
	@JoinColumn(name="skill_code", insertable=false, updatable=false)
	private Skill skill;	

	public MudBeingSkillPK() {
	}
	public Integer getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((skill == null) ? 0 : skill.hashCode());
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
		MudBeingSkillPK other = (MudBeingSkillPK) obj;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		if (skill == null) {
			if (other.skill != null)
				return false;
		} else if (!skill.equals(other.skill))
			return false;
		return true;
	}
	
	
}