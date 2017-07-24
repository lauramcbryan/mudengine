package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingAttrPK;


/**
 * The persistent class for the mud_being_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_attr")
public class MudBeingAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingAttrPK id;

	@Column(name="attr_value")
	private Integer attrValue;

	public MudBeingAttr() {
	}

	public MudBeingAttrPK getId() {
		return this.id;
	}

	public void setId(MudBeingAttrPK id) {
		this.id = id;
	}

	public Integer getValue() {
		return this.attrValue;
	}

	public void setValue(Integer attrOffset) {
		this.attrValue = attrOffset;
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
		MudBeingAttr other = (MudBeingAttr) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}