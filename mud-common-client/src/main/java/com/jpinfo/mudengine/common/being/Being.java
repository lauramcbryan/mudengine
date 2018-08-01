package com.jpinfo.mudengine.common.being;

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
}