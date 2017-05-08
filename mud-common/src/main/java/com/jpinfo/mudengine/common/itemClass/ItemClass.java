package com.jpinfo.mudengine.common.itemClass;

import java.io.Serializable;
import java.util.HashMap;
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

	private Map<String, Float> attrModifiers;

	private Map<String, Float> skillModifiers;

	public ItemClass() {
		
		this.attrModifiers = new HashMap<String, Float>();
		this.skillModifiers = new HashMap<String, Float>();
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

	public Map<String, Float> getAttrModifiers() {
		return attrModifiers;
	}

	public void setAttrModifiers(Map<String, Float> attrModifiers) {
		this.attrModifiers = attrModifiers;
	}

	public Map<String, Float> getSkillModifiers() {
		return skillModifiers;
	}

	public void setSkillModifiers(Map<String, Float> skillModifiers) {
		this.skillModifiers = skillModifiers;
	}
	
	
	

}