package com.jpinfo.mudengine.common.being;

import java.beans.Transient;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
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

	private transient List<BeingAttrModifier> attrModifiers;
	
	private transient List<BeingSkillModifier> skillModifiers;
	
	private String name;
	
	private Integer curPlaceCode;
	
	private String curWorld;
	
	private Long playerId;
	
	private Map<String, Item> equipment;
	
	private Integer quantity;
	

	@Transient	
	public String getClassCode() {
		return beingClass.getCode();
	}
	
	@Transient
	public void setClassCode(String value) {
		beingClass = new BeingClass();
		beingClass.setCode(value);
	}
	
	@Transient
	public Map<String, Integer> getAttrs() {
		
		Map<String, Integer> response = new HashMap<>();
		
		if (this.baseAttrs!=null) {
		
			this.baseAttrs.keySet().stream()
				.forEach(curAttr -> 
					response.put(curAttr, calcEffectiveAttr(
							curAttr,
							this.baseAttrs.get(curAttr),
							this.attrModifiers
							))
					);
			}
		
		return Collections.unmodifiableMap(response);
	}
	
	@Transient
	public Map<String, Integer> getSkills() {

		Map<String, Integer> response = new HashMap<>();
		
		if (this.baseSkills!=null) {
		
			this.baseSkills.keySet().stream()
				.forEach(curSkill -> 
					response.put(curSkill, calcEffectiveSkill(
							curSkill,
							this.baseSkills.get(curSkill),
							this.skillModifiers
							))
					);
			}
		
		return Collections.unmodifiableMap(response);
		
	}
	
	@Transient
	public Float getAttrModifierAmount(String attrCode) {
		return attrModifiers.stream()
				.filter(e -> e.getCode().equals(attrCode))
				.map(BeingAttrModifier::getOffset)
				.reduce(0.0f, (a,b) -> a + b);
	}

	@Transient
	public Float getSkillModifierAmount(String skillCode) {
		return skillModifiers.stream()
				.filter(e -> e.getCode().equals(skillCode))
				.map(BeingSkillModifier::getOffset)
				.reduce(0.0f,  (a,b) -> a + b);
	}
	
	private int calcEffectiveAttr(String attrCode, Integer baseValue, Collection<BeingAttrModifier> attrModifiers) {

		if (attrModifiers!=null) {
			// Traverse all modifier list
			return Math.round( 
				attrModifiers.stream()
					.filter(e -> e.getCode().equals(attrCode))
					.map(BeingAttrModifier::getOffset)
					.reduce(baseValue.floatValue(), (a,b) -> a + b)
				);
		} else {
			return baseValue;
		}
	}

	private int calcEffectiveSkill(String skillCode, Integer baseValue, Collection<BeingSkillModifier> skillModifiers) {
		
		if (skillModifiers!=null) {
			return Math.round( 
				skillModifiers.stream()
					.filter(e -> e.getCode().equals(skillCode))
					.map(BeingSkillModifier::getOffset)
					.reduce(baseValue.floatValue(), (a,b) -> a+b)
					);
		} else {
			return baseValue;
		}

	}
}