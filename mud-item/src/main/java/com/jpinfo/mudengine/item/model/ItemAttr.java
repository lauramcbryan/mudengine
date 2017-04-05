package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.ItemAttrPK;


/**
 * The persistent class for the mud_item_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_attr")
public class ItemAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ItemAttrPK id;

	@Column(name="attr_offset")
	private float attrOffset;

	public ItemAttr() {
	}

	public ItemAttrPK getId() {
		return this.id;
	}

	public void setId(ItemAttrPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.attrOffset;
	}

	public void setOffset(float attrOffset) {
		this.attrOffset = attrOffset;
	}
}