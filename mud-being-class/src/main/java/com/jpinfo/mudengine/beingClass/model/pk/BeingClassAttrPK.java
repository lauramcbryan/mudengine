package com.jpinfo.mudengine.beingClass.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_being_class_attr database table.
 * 
 */
@Embeddable
public class BeingClassAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_class", insertable=false, updatable=false)
	private String beingClass;

	@Column(name="attr_code", insertable=false, updatable=false)
	private String attrCode;

	public BeingClassAttrPK() {
	}
	public String getBeingClass() {
		return this.beingClass;
	}
	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}
	public String getAttrCode() {
		return this.attrCode;
	}
	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeingClassAttrPK)) {
			return false;
		}
		BeingClassAttrPK castOther = (BeingClassAttrPK)other;
		return 
			this.beingClass.equals(castOther.beingClass)
			&& this.attrCode.equals(castOther.attrCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.beingClass.hashCode();
		hash = hash * prime + this.attrCode.hashCode();
		
		return hash;
	}
}