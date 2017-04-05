package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.BeingSkillModifierPK;


/**
 * The persistent class for the mud_being_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_skill_modifier")
public class BeingSkillModifier implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingSkillModifierPK id;

	@Column(name="origin_code")
	private Integer originCode;

	@Column(name="origin_type")
	private String originType;

	@Column(name="skill_offset")
	private float skillOffset;
	
	@Column(name="expires_on")
	private Integer expiresOn;
	

	public BeingSkillModifier() {
	}

	public BeingSkillModifierPK getId() {
		return this.id;
	}

	public void setId(BeingSkillModifierPK id) {
		this.id = id;
	}

	public Integer getOriginCode() {
		return this.originCode;
	}

	public void setOriginCode(Integer originCode) {
		this.originCode = originCode;
	}

	public String getOriginType() {
		return this.originType;
	}

	public void setOriginType(String originType) {
		this.originType = originType;
	}

	public float getOffset() {
		return this.skillOffset;
	}

	public void setOffset(float skillOffset) {
		this.skillOffset = skillOffset;
	}

	public Integer getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(Integer expiresOn) {
		this.expiresOn = expiresOn;
	}
	
	
}