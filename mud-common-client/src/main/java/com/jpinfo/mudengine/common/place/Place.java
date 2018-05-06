package com.jpinfo.mudengine.common.place;

import java.util.*;


import com.jpinfo.mudengine.common.placeClass.PlaceClass;

public class Place {
	
	private Integer placeCode;

	private String placeClassCode;
	
	private PlaceClass placeClass;
	
	private Map<String, PlaceExit> exits;
	
	private Map<String, Integer> attrs;
	
	public Place() {
		this.attrs = new HashMap<String, Integer>();
		this.exits = new HashMap<String, PlaceExit>();
	}
	

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public String getPlaceClassCode() {
		return placeClassCode;
	}

	public void setPlaceClassCode(String placeClass) {
		this.placeClassCode = placeClass;
	}

	public Map<String, PlaceExit> getExits() {
		return exits;
	}

	public void setExits(Map<String, PlaceExit> exits) {
		this.exits = exits;
	}

	public Map<String, Integer> getAttrs() {
		return attrs;
	}


	public void setAttrs(Map<String, Integer> attrs) {
		this.attrs = attrs;
	}
	
	public void setAttr(String attrName, Integer attrValue) {
		this.attrs.put(attrName, attrValue);
	}


	public PlaceClass getPlaceClass() {
		return placeClass;
	}


	public void setPlaceClass(PlaceClass placeClass) {
		this.placeClass = placeClass;
	}

}
