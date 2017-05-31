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

	@Column(name="attr_offset")
	private float attrOffset;

	public MudItemAttr() {
	}

	public MudItemAttrPK getId() {
		return this.id;
	}

	public void setId(MudItemAttrPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.attrOffset;
	}

	public void setOffset(float attrOffset) {
		this.attrOffset = attrOffset;
	}
}