package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the mud_being_items database table.
 * 
 */
@Embeddable
public class MudBeingItemPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Long beingCode;

	@Column(name="item_code")
	private Integer itemCode;

	public MudBeingItemPK() {
	}
	public Long getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}
	public Integer getItemCode() {
		return this.itemCode;
	}
	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MudBeingItemPK)) {
			return false;
		}
		MudBeingItemPK castOther = (MudBeingItemPK)other;
		return 
			this.beingCode.equals(castOther.beingCode)
			&& this.itemCode.equals(castOther.itemCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.beingCode.hashCode();
		hash = hash * prime + this.itemCode.hashCode();
		
		return hash;
	}
}