package com.jpinfo.mudengine.common.item;

import java.io.Serializable;

import java.util.Map;


/**
 * The persistent class for the mud_item database table.
 * 
 */
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer itemCode;

	private String description;

	private String name;

	private Integer usageCount;

	private String itemClass;

	private Map<String, Double> attrModifiers;

	private Map<String, Double> skillModifiers;

	public Item() {
	}

	public Integer getItemCode() {
		return itemCode;
	}

	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
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