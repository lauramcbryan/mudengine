package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mud_being database table.
 * 
 */
@Entity
@Table(name="mud_being")
public class MudBeing implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="being_code")
	private Integer beingCode;

	@Column(name="being_class")
	private String beingClass;

	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingAttr> attributes;

	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingItem> items;

	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingSkill> skills;
	
	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingAttrModifier> attrModifiers;


	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingSkillModifier> skillModifiers;

	@Column
	private String name;
	
	@Column(name="current_world")
	private String curWorld;
	
	@Column(name="current_place")
	private Integer curPlaceCode;
	
	@Column(name="player_id")
	private Integer playerId;

	public MudBeing() {
	}

	public Integer getBeingCode() {
		return this.beingCode;
	}

	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public List<MudBeingAttr> getAttributes() {
		return this.attributes;
	}

	public List<MudBeingItem> getItems() {
		return this.items;
	}

	public List<MudBeingSkill> getSkills() {
		return this.skills;
	}

	public Integer getCurPlaceCode() {
		return curPlaceCode;
	}

	public void setCurPlaceCode(Integer curPlaceCode) {
		this.curPlaceCode = curPlaceCode;
	}

	public String getCurWorld() {
		return curWorld;
	}

	public void setCurWorld(String curWorld) {
		this.curWorld = curWorld;
	}
	
	public String getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}

	public void setAttributes(List<MudBeingAttr> attributes) {
		this.attributes = attributes;
	}

	public void setItems(List<MudBeingItem> items) {
		this.items = items;
	}

	public void setSkills(List<MudBeingSkill> skills) {
		this.skills = skills;
	}

	public List<MudBeingAttrModifier> getAttrModifiers() {
		return attrModifiers;
	}

	public void setAttrModifiers(List<MudBeingAttrModifier> attrModifiers) {
		this.attrModifiers = attrModifiers;
	}

	public List<MudBeingSkillModifier> getSkillModifiers() {
		return skillModifiers;
	}

	public void setSkillModifiers(List<MudBeingSkillModifier> skillModifiers) {
		this.skillModifiers = skillModifiers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}