package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_item_class_attr database table.
 * 
 */
@Embeddable
public class MudItemClassAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_class", insertable=false, updatable=false)
	private String itemClass;

	@Column(name="attr_code")
	private String attrCode;

	public MudItemClassAttrPK() {
	}
	public String getItemClass() {
		return this.itemClass;
	}
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
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
		if (!(other instanceof MudItemClassAttrPK)) {
			return false;
		}
		MudItemClassAttrPK castOther = (MudItemClassAttrPK)other;
		return 
			this.itemClass.equals(castOther.itemClass)
			&& this.attrCode.equals(castOther.attrCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemClass.hashCode();
		hash = hash * prime + this.attrCode.hashCode();
		
		return hash;
	}
}