package com.jpinfo.mudengine.common.being;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;


/**
 * The persistent class for the mud_being database table.
 * 
 */
public class Being implements Serializable, ActionTarget {
	private static final long serialVersionUID = 1L;
	
	private Long beingCode;

	private String beingClass;

	private Map<String, Integer> attrs;
	
	private Map<String, Integer> skills;
	
	private List<BeingAttrModifier> attrModifiers;
	
	private List<BeingSkillModifier> skillModifiers;
	
	private String name;
	
	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long playerId;
	
	private Map<String, Collection<Reaction>> beforeReactions;
	
	private Map<String, Collection<Reaction>> afterReactions;
	
	private Map<String, Item> equipment;
	

	public Being() {
		this.attrs = new HashMap<String, Integer>();
		this.skills = new HashMap<String, Integer>();
		
		this.attrModifiers = new ArrayList<BeingAttrModifier>();
		this.skillModifiers = new ArrayList<BeingSkillModifier>();
	}
	
	

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
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

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
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
	
	public Map<String, Integer> getAttrs() {
		return attrs;
	}

	public Map<String, Integer> getSkills() {
		return skills;
	}

	public Map<String, Item> getEquipment() {
		return equipment;
	}

	public void setEquipment(Map<String, Item> equipment) {
		this.equipment = equipment;
	}



	public Collection<Reaction> getReactions(String actionCode, boolean isBefore) {
		
		if (isBefore) {
			return this.beforeReactions.get(actionCode);
		} else {
			return this.afterReactions.get(actionCode);
		}
	}

}