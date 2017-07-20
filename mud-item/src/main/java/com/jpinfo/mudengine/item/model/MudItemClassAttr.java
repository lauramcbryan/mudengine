package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.MudItemClassAttrPK;


/**
 * The persistent class for the mud_item_class_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_class_attr")
public class MudItemClassAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudItemClassAttrPK id;

	@Column(name="attr_value")
	private Integer attrValue;

	public MudItemClassAttr() {
	}

	public MudItemClassAttrPK getId() {
		return this.id;
	}

	public void setId(MudItemClassAttrPK id) {
		this.id = id;
	}

	public Integer getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(Integer attrValue) {
		this.attrValue = attrValue;
	}
}