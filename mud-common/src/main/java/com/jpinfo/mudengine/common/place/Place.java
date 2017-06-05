package com.jpinfo.mudengine.common.place;

import java.util.*;


import com.jpinfo.mudengine.common.interfaces.ActionTarget;

public class Place implements ActionTarget {
	
	private Integer placeCode;

	private String placeClassCode;
	
	private Map<String, PlaceExits> exits;
	
	public Place() {
		this.exits = new HashMap<String, PlaceExits>();
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

	public Map<String, PlaceExits> getExits() {
		return exits;
	}

	public void setExits(Map<String, PlaceExits> exits) {
		this.exits = exits;
	}
}
