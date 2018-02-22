package com.jpinfo.mudengine.common.being;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.common.item.Item;


/**
 * The persistent class for the mud_being database table.
 * 
 */
public class Being implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final Integer BEING_TYPE_REGULAR_NON_SENTIENT = 0;
	public static final Integer BEING_TYPE_REGULAR_SENTIENT = 1;
	public static final Integer BEING_TYPE_NPC = 2;
	public static final Integer BEING_TYPE_PLAYER = 3;
	
	private Long beingCode;
	
	private Integer beingType;

	private String beingClassCode;
	
	private BeingClass beingClass;
	
	private Map<String, Integer> baseAttrs;
	
	private Map<String, Integer> baseSkills;

	private Map<String, Integer> attrs;
	
	private Map<String, Integer> skills;
	
	private List<BeingAttrModifier> attrModifiers;
	
	private List<BeingSkillModifier> skillModifiers;
	
	private String name;
	
	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long playerId;
	
	private Map<String, Item> equipment;
	
	private Integer quantity;
	

	public Being() {
		this.attrs = new HashMap<String, Integer>();
		this.skills = new HashMap<String, Integer>();
		
		//this.attrModifiers = new ArrayList<BeingAttrModifier>();
		//this.skillModifiers = new ArrayList<BeingSkillModifier>();
	}

	public Long getBeingCode() {
		return beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public String getBeingClassCode() {
		return beingClassCode;
	}

	public void setBeingClassCode(String beingClassCode) {
		this.beingClassCode = beingClassCode;
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


	public Integer getBeingType() {
		return beingType;
	}



	public void setBeingType(Integer beingType) {
		this.beingType = beingType;
	}



	public Integer getQuantity() {
		return quantity;
	}



	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



	public Map<String, Integer> getBaseAttrs() {
		return baseAttrs;
	}



	public void setBaseAttrs(Map<String, Integer> baseAttrs) {
		this.baseAttrs = baseAttrs;
	}



	public Map<String, Integer> getBaseSkills() {
		return baseSkills;
	}



	public void setBaseSkills(Map<String, Integer> baseSkills) {
		this.baseSkills = baseSkills;
	}



	public void setBeingClass(BeingClass beingClass) {
		this.beingClass = beingClass;
	}
	
	public BeingClass getBeingClass() {
		return this.beingClass;
	}
}