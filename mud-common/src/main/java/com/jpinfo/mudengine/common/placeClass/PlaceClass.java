package com.jpinfo.mudengine.common.placeClass;

import java.util.HashMap;
import java.util.Map;

import com.jpinfo.mudengine.common.interfaces.ActionTarget;

public class PlaceClass implements ActionTarget {
	
	private String placeClassCode;

	private String name;
	
	private String description;
	
	private Integer sizeCapacity;
	
	private Integer weightCapacity;

	private Map<String, Double> attrModifiers;
	
	private Map<String, Double> skillModifiers;
	
	private String parentClassCode;

	private Integer buildCost;
	
	private Integer buildEffort;
	
	private String material;
	
	
	public PlaceClass() {
		this.attrModifiers = new HashMap<String, Double>();
		this.skillModifiers = new HashMap<String, Double>();
	}

	
	public String getPlaceClassCode() {
		return placeClassCode;
	}

	public void setPlaceClassCode(String placeClassCode) {
		this.placeClassCode = placeClassCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSizeCapacity() {
		return sizeCapacity;
	}

	public void setSizeCapacity(Integer sizeCapacity) {
		this.sizeCapacity = sizeCapacity;
	}

	public Integer getWeightCapacity() {
		return weightCapacity;
	}

	public void setWeightCapacity(Integer weightCapacity) {
		this.weightCapacity = weightCapacity;
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

	public String getParentClassCode() {
		return parentClassCode;
	}

	public void setParentClassCode(String parentClassCode) {
		this.parentClassCode = parentClassCode;
	}

	public Integer getBuildCost() {
		return buildCost;
	}

	public void setBuildCost(Integer buildCost) {
		this.buildCost = buildCost;
	}

	public Integer getBuildEffort() {
		return buildEffort;
	}

	public void setBuildEffort(Integer buildEffort) {
		this.buildEffort = buildEffort;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
}
