package com.jpinfo.mudengine.beingClass.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.beingClass.model.pk.BeingClassAttrPK;


/**
 * The persistent class for the mud_being_class_attr database table.
 * 
 */
@Entity
@Table(name="mud_being_class_attr")
public class MudBeingClassAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingClassAttrPK id;

	@Column(name="attr_value")
	private Integer attrValue;

	public MudBeingClassAttr() {
	}

	public BeingClassAttrPK getId() {
		return this.id;
	}

	public void setId(BeingClassAttrPK id) {
		this.id = id;
	}

	public Integer getAttrValue() {
		return this.attrValue;
	}

	public void setAttrValue(Integer attrValue) {
		this.attrValue = attrValue;
	}
}