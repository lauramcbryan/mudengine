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
	
	private Map<String, Long> baseAttrs;
	
	private Map<String, Long> baseSkills;

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
	public Map<String, Long> getAttrs() {
		
		Map<String, Long> response = new HashMap<>();
		
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
	public Map<String, Long> getSkills() {

		Map<String, Long> response = new HashMap<>();
		
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
	public Double getAttrModifierAmount(String attrCode) {
		return attrModifiers.stream()
				.filter(e -> e.getCode().equals(attrCode))
				.mapToDouble(BeingAttrModifier::getOffset)
				.sum();
	}

	@Transient
	public Double getSkillModifierAmount(String skillCode) {
		return skillModifiers.stream()
				.filter(e -> e.getCode().equals(skillCode))
				.mapToDouble(BeingSkillModifier::getOffset)
				.sum();
	}
	
	private long calcEffectiveAttr(String attrCode, Long baseValue, Collection<BeingAttrModifier> attrModifiers) {

		if (attrModifiers!=null) {
			// Traverse all modifier list
			return Math.round( baseValue + 
				attrModifiers.stream()
					.filter(e -> e.getCode().equals(attrCode))
					.mapToDouble(BeingAttrModifier::getOffset)
					.sum()
				);
		} else {
			return baseValue;
		}
	}

	private long calcEffectiveSkill(String skillCode, Long baseValue, Collection<BeingSkillModifier> skillModifiers) {
		
		if (skillModifiers!=null) {
			return Math.round(baseValue + 
				skillModifiers.stream()
					.filter(e -> e.getCode().equals(skillCode))
					.mapToDouble(BeingSkillModifier::getOffset)
					.sum()
					);
		} else {
			return baseValue;
		}

	}
}