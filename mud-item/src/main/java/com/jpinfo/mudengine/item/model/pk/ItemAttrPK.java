package com.jpinfo.mudengine.item.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_item_attr database table.
 * 
 */
@Embeddable
public class ItemAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_code", insertable=false, updatable=false)
	private Integer itemCode;

	@Column(name="attr_code")
	private String attrCode;

	public ItemAttrPK() {
	}
	public Integer getItemCode() {
		return this.itemCode;
	}
	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
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
		if (!(other instanceof ItemAttrPK)) {
			return false;
		}
		ItemAttrPK castOther = (ItemAttrPK)other;
		return 
			this.itemCode.equals(castOther.itemCode)
			&& this.attrCode.equals(castOther.attrCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemCode.hashCode();
		hash = hash * prime + this.attrCode.hashCode();
		
		return hash;
	}
}