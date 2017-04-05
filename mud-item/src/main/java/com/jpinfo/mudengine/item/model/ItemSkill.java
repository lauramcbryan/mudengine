package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.item.model.pk.ItemSkillPK;


/**
 * The persistent class for the mud_item_skill database table.
 * 
 */
@Entity
@Table(name="mud_item_skill")
public class ItemSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ItemSkillPK id;

	@Column(name="skill_offset")
	private float skillOffset;

	public ItemSkill() {
	}

	public ItemSkillPK getId() {
		return this.id;
	}

	public void setId(ItemSkillPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.skillOffset;
	}

	public void setOffset(float skillOffset) {
		this.skillOffset = skillOffset;
	}
}