package com.jpinfo.mudengine.common.place;

import java.util.*;


import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;

public class Place implements ActionTarget {
	
	private Integer placeCode;

	private String placeClassCode;
	
	private Map<String, PlaceExit> exits;
	
	private Map<String, Integer> attrs;
	
	private Map<String, Collection<Reaction>> beforeReactions;
	
	private Map<String, Collection<Reaction>> afterReactions;
	
	
	public Place() {
		this.attrs = new HashMap<String, Integer>();
		this.exits = new HashMap<String, PlaceExit>();
		this.beforeReactions = new HashMap<String, Collection<Reaction>>();
		this.afterReactions = new HashMap<String, Collection<Reaction>>();
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
	
	public Collection<Reaction> getReactions(String actionCode, boolean isBefore) {
		
		if (isBefore) {
			return this.beforeReactions.get(actionCode);
		} else {
			return this.afterReactions.get(actionCode);
		}
	}


	public Map<String, Integer> getAttrs() {
		return attrs;
	}


	public void setAttrs(Map<String, Integer> attrs) {
		this.attrs = attrs;
	}

	
	
}
