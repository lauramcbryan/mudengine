package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.BeingAttrModifierPK;


/**
 * The persistent class for the mud_being_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_attr_modifier")
public class BeingAttrModifier implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingAttrModifierPK id;

	@Column(name="attr_offset")
	private float attrOffset;

	@Column(name="origin_code")
	private Integer originCode;

	@Column(name="origin_type")
	private String originType;
	
	public Integer getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(Integer expiresOn) {
		this.expiresOn = expiresOn;
	}

	@Column(name="expires_on")
	private Integer expiresOn;

	public BeingAttrModifier() {
	}

	public BeingAttrModifierPK getId() {
		return this.id;
	}

	public void setId(BeingAttrModifierPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.attrOffset;
	}

	public void setOffset(float attrOffset) {
		this.attrOffset = attrOffset;
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
}