package com.jpinfo.mudengine.common.being;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;


/**
 * The persistent class for the mud_being_class database table.
 * 
 */
@Data
public class BeingClass implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String beingClassCode;

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
}