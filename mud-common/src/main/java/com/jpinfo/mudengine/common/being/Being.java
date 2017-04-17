package com.jpinfo.mudengine.common.being;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.common.interfaces.ActionTarget;


/**
 * The persistent class for the mud_being database table.
 * 
 */
public class Being implements Serializable, ActionTarget {
	private static final long serialVersionUID = 1L;

	private Integer beingCode;

	private String beingClass;

	private Map<String, Float> attrs;
	
	private Map<String, Float> skills;
	
	private List<BeingAttrModifier> attrModifiers;
	
	private List<BeingSkillModifier> skillModifiers;

	private Map<Integer, BeingItem> items;
	
	private String name;
	
	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Integer playerId;

	public Being() {
		this.attrs = new HashMap<String, Float>();
		this.skills = new HashMap<String, Float>();
		this.items = new HashMap<Integer, BeingItem>();
		
		this.attrModifiers = new ArrayList<BeingAttrModifier>();
		this.skillModifiers = new ArrayList<BeingSkillModifier>();
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

	public Map<String, Float> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Float> attrs) {
		this.attrs = attrs;
	}

	public Map<String, Float> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, Float> skills) {
		this.skills = skills;
	}

	public Map<Integer, BeingItem> getItems() {
		return items;
	}

	public void setItems(Map<Integer, BeingItem> items) {
		this.items = items;
	}

	public String getCurWorld() {
		return curWorld;
	}

	public void setCurWorld(String curWorld) {
		this.curWorld = curWorld;
	}

	public Integer getCurPlaceCode() {
		return curPlaceCode;
	}

	public void setCurPlaceCode(Integer curPlaceCode) {
		this.curPlaceCode = curPlaceCode;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public List<BeingAttrModifier> getAttrModifiers() {
		return attrModifiers;
	}

	public void setAttrModifiers(List<BeingAttrModifier> attrModifiers) {
		this.attrModifiers = attrModifiers;
	}

	public List<BeingSkillModifier> getSkillModifiers() {
		return skillModifiers;
	}

	public void setSkillModifiers(List<BeingSkillModifier> skillModifiers) {
		this.skillModifiers = skillModifiers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}