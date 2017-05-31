package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_being_skills database table.
 * 
 */
@Embeddable
public class MudBeingSkillModifierPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Long beingCode;

	@Column(name="skill_code", insertable=false, updatable=false)
	private String skillCode;
	
	@Column(name="origin_code")
	private String originCode;

	@Column(name="origin_type")
	private String originType;
	

	public MudBeingSkillModifierPK() {
	}

	public Long getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public String getSkillCode() {
		return skillCode;
	}

	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

	public String getOriginCode() {
		return originCode;
	}

	public void setOriginCode(String originCode) {
		this.originCode = originCode;
	}

	public String getOriginType() {
		return originType;
	}

	public void setOriginType(String originType) {
		this.originType = originType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
		result = prime * result + ((originCode == null) ? 0 : originCode.hashCode());
		result = prime * result + ((originType == null) ? 0 : originType.hashCode());
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
		MudBeingSkillModifierPK other = (MudBeingSkillModifierPK) obj;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		if (originCode == null) {
			if (other.originCode != null)
				return false;
		} else if (!originCode.equals(other.originCode))
			return false;
		if (originType == null) {
			if (other.originType != null)
				return false;
		} else if (!originType.equals(other.originType))
			return false;
		if (skillCode == null) {
			if (other.skillCode != null)
				return false;
		} else if (!skillCode.equals(other.skillCode))
			return false;
		return true;
	}
	
	
}