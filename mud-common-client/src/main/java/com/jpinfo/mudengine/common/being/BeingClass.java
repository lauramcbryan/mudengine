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
	
	private String code;

	private String description;

	private String name;

	private Integer size;

	private Integer weightCapacity;

	private Map<String, Long> attrs;

	private Map<String, Long> skills;
	
	
	public BeingClass() {
	
		this.attrs = new HashMap<>();
		this.skills = new HashMap<>();
	}
}