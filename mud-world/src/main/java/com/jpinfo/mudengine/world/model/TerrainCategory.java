package com.jpinfo.mudengine.world.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="MUD_TERRAIN_CATEGORY")
public class TerrainCategory {

	@Id
	@Column(name="CATEGORY_CODE")
	private Integer categoryCode;
	
	@Column
	private String name;
	
	@Column
	private String description;
		
	@OneToMany(mappedBy="pk.categoryCode", fetch=FetchType.EAGER)
	private Set<TerrainCategoryAttr> attrModifiers;
	
	@OneToMany(mappedBy="pk.categoryCode", fetch=FetchType.EAGER)
	private Set<TerrainCategorySkill> skillModifiers;

	

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<TerrainCategoryAttr> getAttrModifiers() {
		return attrModifiers;
	}

	public void setAttrModifiers(Set<TerrainCategoryAttr> attrModifiers) {
		this.attrModifiers = attrModifiers;
	}

	public Set<TerrainCategorySkill> getSkillModifiers() {
		return skillModifiers;
	}

	public void setSkillModifiers(Set<TerrainCategorySkill> skillModifiers) {
		this.skillModifiers = skillModifiers;
	}

	
}
