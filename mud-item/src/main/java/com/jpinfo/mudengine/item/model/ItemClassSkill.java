package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.ItemClassSkillPK;


/**
 * The persistent class for the mud_item_class_skill database table.
 * 
 */
@Entity
@Table(name="mud_item_class_skill")
public class ItemClassSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ItemClassSkillPK id;

	@Column(name="skill_offset")
	private float skillOffset;

	public ItemClassSkill() {
	}

	public ItemClassSkillPK getId() {
		return this.id;
	}

	public void setId(ItemClassSkillPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.skillOffset;
	}

	public void setOffset(float skillOffset) {
		this.skillOffset = skillOffset;
	}
}