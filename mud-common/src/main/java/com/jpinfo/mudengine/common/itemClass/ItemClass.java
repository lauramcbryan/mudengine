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

	private float size;

	private float weight;
	
	private String description;

	private Map<String, Integer> attrs;

	public ItemClass() {
		
		this.attrs = new HashMap<String, Integer>();
	}

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
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

	public Map<String, Integer> getAttrs() {
		return attrs;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}