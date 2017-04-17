package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_item_skill database table.
 * 
 */
@Embeddable
public class MudItemSkillPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_code", insertable=false, updatable=false)
	private Integer itemCode;

	@Column(name="skill_code")
	private String skillCode;

	public MudItemSkillPK() {
	}
	public Integer getItemCode() {
		return this.itemCode;
	}
	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
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
		if (!(other instanceof MudItemSkillPK)) {
			return false;
		}
		MudItemSkillPK castOther = (MudItemSkillPK)other;
		return 
			this.itemCode.equals(castOther.itemCode)
			&& this.skillCode.equals(castOther.skillCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemCode.hashCode();
		hash = hash * prime + this.skillCode.hashCode();
		
		return hash;
	}
}