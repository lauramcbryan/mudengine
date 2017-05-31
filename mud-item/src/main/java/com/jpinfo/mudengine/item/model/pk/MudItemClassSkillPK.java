package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_item_class_skill database table.
 * 
 */
@Embeddable
public class MudItemClassSkillPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_class", insertable=false, updatable=false)
	private String itemClass;

	@Column(name="skill_code")
	private String skillCode;

	public MudItemClassSkillPK() {
	}
	public String getItemClass() {
		return this.itemClass;
	}
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
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
		if (!(other instanceof MudItemClassSkillPK)) {
			return false;
		}
		MudItemClassSkillPK castOther = (MudItemClassSkillPK)other;
		return 
			this.itemClass.equals(castOther.itemClass)
			&& this.skillCode.equals(castOther.skillCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemClass.hashCode();
		hash = hash * prime + this.skillCode.hashCode();
		
		return hash;
	}
}