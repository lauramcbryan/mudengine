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
public class BeingAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingAttrPK id;

	@Column(name="attr_value")
	private float attrValue;

	public BeingAttr() {
	}

	public MudBeingAttrPK getId() {
		return this.id;
	}

	public void setId(MudBeingAttrPK id) {
		this.id = id;
	}

	public float getValue() {
		return this.attrValue;
	}

	public void setValue(float attrOffset) {
		this.attrValue = attrOffset;
	}
}