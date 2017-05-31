package com.jpinfo.mudengine.world.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PlaceClassSkillPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="PLACE_CLASS_CODE")
	private String placeClassCode;

	@Column(name="SKILL_CODE")
	private String skillCode;

	public String getPlaceClassCode() {
		return placeClassCode;
	}

	public void setPlaceClassCode(String placeClassCode) {
		this.placeClassCode = placeClassCode;
	}

	public String getSkillCode() {
		return skillCode;
	}

	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((placeClassCode == null) ? 0 : placeClassCode.hashCode());
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
		PlaceClassSkillPK other = (PlaceClassSkillPK) obj;
		if (placeClassCode == null) {
			if (other.placeClassCode != null)
				return false;
		} else if (!placeClassCode.equals(other.placeClassCode))
			return false;
		if (skillCode == null) {
			if (other.skillCode != null)
				return false;
		} else if (!skillCode.equals(other.skillCode))
			return false;
		return true;
	}
	
	
}
