package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.MudItemAttrPK;


/**
 * The persistent class for the mud_item_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_attr")
public class MudItemAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudItemAttrPK id;

	@Column(name="attr_value")
	private Integer attrValue;

	public MudItemAttr() {
	}

	public MudItemAttrPK getId() {
		return this.id;
	}

	public void setId(MudItemAttrPK id) {
		this.id = id;
	}

	public Integer getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(Integer attrValue) {
		this.attrValue = attrValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MudItemAttr other = (MudItemAttr) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}