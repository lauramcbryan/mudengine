package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.ItemClassAttrPK;


/**
 * The persistent class for the mud_item_class_attr database table.
 * 
 */
@Entity
@Table(name="mud_item_class_attr")
public class ItemClassAttr implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ItemClassAttrPK id;

	@Column(name="attr_offset")
	private float attrOffset;

	public ItemClassAttr() {
	}

	public ItemClassAttrPK getId() {
		return this.id;
	}

	public void setId(ItemClassAttrPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.attrOffset;
	}

	public void setOffset(float attrOffset) {
		this.attrOffset = attrOffset;
	}
}