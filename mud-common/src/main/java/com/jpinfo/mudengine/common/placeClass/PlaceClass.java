package com.jpinfo.mudengine.common.placeClass;

import java.util.HashMap;
import java.util.Map;


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

	public Map<String, Integer> getAttrs() {
		return attrs;
	}


	public void setAttrs(Map<String, Integer> attrs) {
		this.attrs = attrs;
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

	public String getDemisePlaceClassCode() {
		return demisePlaceClassCode;
	}


	public void setDemisePlaceClassCode(String demisePlaceClassCode) {
		this.demisePlaceClassCode = demisePlaceClassCode;
	}
	
	
}
