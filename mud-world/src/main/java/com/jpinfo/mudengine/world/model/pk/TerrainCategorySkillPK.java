package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TerrainCategorySkillPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="CATEGORY_CODE")
	private Integer categoryCode;

	@Column(name="SKILL_CODE")
	private Integer skillCode;

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public void setCategory(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Integer getSkillCode() {
		return skillCode;
	}

	public void setSkillCode(Integer skillCode) {
		this.skillCode = skillCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryCode == null) ? 0 : categoryCode.hashCode());
		result = prime * result + ((skillCode == null) ? 0 : skillCode.hashCode());
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
		TerrainCategorySkillPK other = (TerrainCategorySkillPK) obj;
		if (categoryCode == null) {
			if (other.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(other.categoryCode))
			return false;
		if (skillCode == null) {
			if (other.skillCode != null)
				return false;
		} else if (!skillCode.equals(other.skillCode))
			return false;
		return true;
	}
	
	
	
}
