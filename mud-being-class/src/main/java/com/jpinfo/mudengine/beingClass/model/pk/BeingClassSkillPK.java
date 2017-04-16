package com.jpinfo.mudengine.beingClass.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_being_class_skills database table.
 * 
 */
@Embeddable
public class BeingClassSkillPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_class", insertable=false, updatable=false)
	private String beingClass;

	@Column(name="skill_code", insertable=false, updatable=false)
	private String skillCode;

	public BeingClassSkillPK() {
	}
	public String getBeingClass() {
		return this.beingClass;
	}
	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}
	public String getSkillCode() {
		return this.skillCode;
	}
	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeingClassSkillPK)) {
			return false;
		}
		BeingClassSkillPK castOther = (BeingClassSkillPK)other;
		return 
			this.beingClass.equals(castOther.beingClass)
			&& this.skillCode.equals(castOther.skillCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.beingClass.hashCode();
		hash = hash * prime + this.skillCode.hashCode();
		
		return hash;
	}
}