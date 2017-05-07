package com.jpinfo.mudengine.common.beingClass;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * The persistent class for the mud_being_class database table.
 * 
 */
public class BeingClass implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String SERVICE_NAME="mud-being-class";
	public static final String SERVICE_GET_URL="/being/class/{id}";

	private String beingClass;

	private String description;

	private String name;

	private Integer size;

	private Integer weightCapacity;

	private Map<String, Integer> attributes;

	private Map<String, Integer> skills;
	
	
	public BeingClass() {
	
		this.attributes = new HashMap<String, Integer>();
		this.skills = new HashMap<String, Integer>();
	}

	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getWeightCapacity() {
		return weightCapacity;
	}

	public void setWeightCapacity(Integer weightCapacity) {
		this.weightCapacity = weightCapacity;
	}

	public Map<String, Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Integer> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Integer> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, Integer> skills) {
		this.skills = skills;
	}
	
	
}