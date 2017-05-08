package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mud_item_class database table.
 * 
 */
@Entity
@Table(name="mud_item_class")
public class MudItemClass implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="item_class")
	private String itemClass;

	private Integer durability;

	private float size;

	private float weight;

	//bi-directional many-to-one association to ItemClassAttr
	@OneToMany(mappedBy="id.itemClass", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudItemClassAttr> attrs;

	//bi-directional many-to-one association to ItemClassSkill
	@OneToMany(mappedBy="id.itemClass", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudItemClassSkill> skills;

	public MudItemClass() {
	}

	public String getItemClass() {
		return this.itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public Integer getDurability() {
		return this.durability;
	}

	public void setDurability(Integer durability) {
		this.durability = durability;
	}

	public float getSize() {
		return this.size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getWeight() {
		return this.weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public List<MudItemClassAttr> getAttrs() {
		return this.attrs;
	}

	public List<MudItemClassSkill> getSkills() {
		return this.skills;
	}
}