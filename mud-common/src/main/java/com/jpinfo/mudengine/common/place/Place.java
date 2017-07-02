package com.jpinfo.mudengine.common.place;

import java.util.*;


import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;

public class Place implements ActionTarget {
	
	private Integer placeCode;

	private String placeClassCode;
	
	private Map<String, PlaceExits> exits;
	
	private Map<String, Collection<Reaction>> beforeReactions;
	
	private Map<String, Collection<Reaction>> afterReactions;
	
	
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
	
	public Collection<Reaction> getReactions(String actionCode, boolean isBefore) {
		
		if (isBefore) {
			return this.beforeReactions.get(actionCode);
		} else {
			return this.afterReactions.get(actionCode);
		}
	}

	
}
