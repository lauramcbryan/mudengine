package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the mud_skill_category database table.
 * 
 */
@Entity
@Table(name="mud_skill_category")
public class MudSkillCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="category_code")
	private String categoryCode;

	private String description;

	private String name;

	@Column(name="attr_code_based")
	private String attrBasedOn;

	public MudSkillCategory() {
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttrBasedOn() {
		return attrBasedOn;
	}

	public void setAttrBasedOn(String attrBasedOn) {
		this.attrBasedOn = attrBasedOn;
	}
	
	
}