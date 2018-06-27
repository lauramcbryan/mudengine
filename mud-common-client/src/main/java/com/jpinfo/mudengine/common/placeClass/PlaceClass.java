package com.jpinfo.mudengine.common.placeClass;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PlaceClass {
	
	private String placeClassCode;

	private String name;
	
	private String description;
	
	private Integer sizeCapacity;
	
	private Integer weightCapacity;

	private Map<String, Integer> attrs;
	
	private String parentClassCode;
	
	private String demisePlaceClassCode;

	private Integer buildCost;
	
	private Integer buildEffort;
	
	public PlaceClass() {
		this.attrs = new HashMap<String, Integer>();
	}	
}
