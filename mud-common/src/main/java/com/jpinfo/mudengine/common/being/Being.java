package com.jpinfo.mudengine.common.being;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * The persistent class for the mud_being database table.
 * 
 */
public class Being implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer beingCode;

	private String beingClass;

	private Map<String, Integer> attrs;
	
	private Map<String, Integer> skills;

	private Map<Integer, BeingItem> items;
	
	private String lastWorld;
	
	private Integer playerId;

	public Being() {
		this.attrs = new HashMap<String, Integer>();
		this.skills = new HashMap<String, Integer>();
		this.items = new HashMap<Integer, BeingItem>();
	}

	public Integer getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}

	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}

	public Map<String, Integer> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Integer> attrs) {
		this.attrs = attrs;
	}

	public Map<String, Integer> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, Integer> skills) {
		this.skills = skills;
	}

	public Map<Integer, BeingItem> getItems() {
		return items;
	}

	public void setItems(Map<Integer, BeingItem> items) {
		this.items = items;
	}

	public String getLastWorld() {
		return lastWorld;
	}

	public void setLastWorld(String lastWorld) {
		this.lastWorld = lastWorld;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	
}