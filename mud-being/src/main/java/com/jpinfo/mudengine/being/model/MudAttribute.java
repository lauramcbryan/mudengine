package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the mud_attribute database table.
 * 
 */
@Entity
@Table(name="mud_attribute")
public class MudAttribute implements Serializable {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrCode == null) ? 0 : attrCode.hashCode());
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
		MudAttribute other = (MudAttribute) obj;
		if (attrCode == null) {
			if (other.attrCode != null)
				return false;
		} else if (!attrCode.equals(other.attrCode))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="attr_code")
	private String attrCode;

	private String description;

	private String name;

	public MudAttribute() {
	}

	public String getAttrCode() {
		return this.attrCode;
	}

	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}