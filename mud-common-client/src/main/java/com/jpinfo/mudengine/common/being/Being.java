package com.jpinfo.mudengine.common.being;

import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpinfo.mudengine.common.item.Item;

import lombok.Data;


/**
 * The persistent class for the mud_being database table.
 * 
 */
@Data
public class Being implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum enumBeingType {REGULAR_NON_SENTIENT, REGULAR_SENTIENT, NPC, PLAYABLE}
	
	private Long code;
	
	private enumBeingType type;

	private BeingClass beingClass;
	
	private Map<String, Integer> baseAttrs;
	
	private Map<String, Integer> baseSkills;

	private Map<String, Integer> attrs;
	
	private Map<String, Integer> skills;
	
	private transient List<BeingAttrModifier> attrModifiers;
	
	private transient List<BeingSkillModifier> skillModifiers;
	
	private String name;
	
	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long playerId;
	
	private Map<String, Item> equipment;
	
	private Integer quantity;
	

	public Being() {
		this.attrs = new HashMap<>();
		this.skills = new HashMap<>();
	}
	
	@Transient	
	public String getClassCode() {
		return beingClass.getCode();
	}
	
	@Transient
	public void setClassCode(String value) {
		beingClass = new BeingClass();
		beingClass.setCode(value);
	}
}