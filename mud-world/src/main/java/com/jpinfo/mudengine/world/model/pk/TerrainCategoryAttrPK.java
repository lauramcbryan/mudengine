package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TerrainCategoryAttrPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="CATEGORY_CODE")
	private Integer categoryCode;

	@Column(name="ATTR_CODE")
	private Integer attrCode;

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public void setCategory(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Integer getAttrCode() {
		return attrCode;
	}

	public void setAttrCode(Integer attrCode) {
		this.attrCode = attrCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrCode == null) ? 0 : attrCode.hashCode());
		result = prime * result + ((categoryCode == null) ? 0 : categoryCode.hashCode());
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
		TerrainCategoryAttrPK other = (TerrainCategoryAttrPK) obj;
		if (attrCode == null) {
			if (other.attrCode != null)
				return false;
		} else if (!attrCode.equals(other.attrCode))
			return false;
		if (categoryCode == null) {
			if (other.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(other.categoryCode))
			return false;
		return true;
	}
	
	
	
}
