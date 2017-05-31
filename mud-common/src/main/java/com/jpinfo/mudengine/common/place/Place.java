package com.jpinfo.mudengine.common.place;

import java.util.*;


import com.jpinfo.mudengine.common.interfaces.ActionTarget;

public class Place implements ActionTarget {
	
	private Integer placeCode;

	private String placeClassCode;
	
	private Set<PlaceBeings> beings;
	
	private Set<PlaceItems> items;
	
	private Map<String, PlaceExits> exits;
	
	public Place() {
		this.beings = new HashSet<PlaceBeings>();
		this.items = new HashSet<PlaceItems>();
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


	public Set<PlaceBeings> getBeings() {
		return beings;
	}


	public void setBeings(Set<PlaceBeings> beings) {
		this.beings = beings;
	}


	public Set<PlaceItems> getItems() {
		return items;
	}


	public void setItems(Set<PlaceItems> items) {
		this.items = items;
	}
	
}
