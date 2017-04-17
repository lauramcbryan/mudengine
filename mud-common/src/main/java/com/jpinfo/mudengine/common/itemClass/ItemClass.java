package com.jpinfo.mudengine.common.itemClass;

import java.io.Serializable;

import java.util.Map;


/**
 * The persistent class for the mud_item_class database table.
 * 
 */
public class ItemClass implements Serializable {
	private static final long serialVersionUID = 1L;

	private String itemClass;

	private Integer durability;

	private float size;

	private float weight;

	private Map<String, Double> attrModifiers;

	private Map<String, Double> skillModifiers;

	public ItemClass() {
	}

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public Integer getDurability() {
		return durability;
	}

	public void setDurability(Integer durability) {
		this.durability = durability;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Map<String, Double> getAttrModifiers() {
		return attrModifiers;
	}

	public void setAttrModifiers(Map<String, Double> attrModifiers) {
		this.attrModifiers = attrModifiers;
	}

	public Map<String, Double> getSkillModifiers() {
		return skillModifiers;
	}

	public void setSkillModifiers(Map<String, Double> skillModifiers) {
		this.skillModifiers = skillModifiers;
	}
	
	
	

}