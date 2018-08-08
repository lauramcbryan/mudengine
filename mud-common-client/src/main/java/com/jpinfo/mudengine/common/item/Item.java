package com.jpinfo.mudengine.common.item;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jpinfo.mudengine.common.itemclass.ItemClass;

import lombok.Data;

@Data
public class Item implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long itemCode;
	
	private String itemName;

	private ItemClass itemClass;
	
	private Integer quantity;

	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long curOwner;
	
	private Map<String, Integer> attrs;
	
	public Item() {
		this.attrs = new HashMap<>();
	}
	
	@JsonIgnore
	public String getItemClassCode() {
		return itemClass.getItemClassCode();
	}
	
	public void setItemClassCode(String classCode) {
		this.itemClass = new ItemClass();
		this.itemClass.setItemClassCode(classCode);
	}
}