package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.Attribute;

/**
 * The primary key class for the mud_being_attr database table.
 * 
 */
@Embeddable
public class MudBeingAttrPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Integer beingCode;
	
	@ManyToOne
	@JoinColumn(name="attr_code", insertable=false, updatable=false)
	private Attribute attribute;
	

	public MudBeingAttrPK() {
	}
	public Integer getBeingCode() {
		return this.beingCode;
	}
	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((beingCode == null) ? 0 : beingCode.hashCode());
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
		MudBeingAttrPK other = (MudBeingAttrPK) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (beingCode == null) {
			if (other.beingCode != null)
				return false;
		} else if (!beingCode.equals(other.beingCode))
			return false;
		return true;
	}
	
	

}