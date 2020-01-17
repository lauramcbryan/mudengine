package com.jpinfo.mudengine.common.item;


import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.jpinfo.mudengine.common.itemclass.ItemClass;

import lombok.Data;

@Data
public class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long code;
	
	private String name;

	private ItemClass itemClass;
	
	private Integer quantity;

	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long curOwner;
	
	private Map<String, Integer> attrs;
	
	public Item() {
		this.attrs = new HashMap<>();
	}

	@Transient
	public String getClassCode() {
		return itemClass.getCode();
	}

	@Transient
	public void setClassCode(String classCode) {
		this.itemClass = new ItemClass();
		this.itemClass.setCode(classCode);
	}

}