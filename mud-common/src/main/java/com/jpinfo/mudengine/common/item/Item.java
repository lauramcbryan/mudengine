package com.jpinfo.mudengine.common.item;

import java.io.Serializable;


import java.util.Map;

import com.jpinfo.mudengine.common.interfaces.ActionTarget;

public class Item implements Serializable, ActionTarget {
	private static final long serialVersionUID = 1L;
	
	public static final String SERVICE_NAME="mud-item";
	
	public static final String SERVICE_GET_URL="/item/{id}";
	public static final String SERVICE_CREATE_URL="/item";
	public static final String SERVICE_UPDATE_URL="/item/{id}";
	

	private Integer itemCode;

	private String description;

	private String name;

	private Integer usageCount;

	private String itemClass;

	private Map<String, Float> attrModifiers;

	private Map<String, Float> skillModifiers;

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