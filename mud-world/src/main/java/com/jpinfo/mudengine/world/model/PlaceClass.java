package com.jpinfo.mudengine.world.model;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="MUD_PLACE_CLASS")
public class PlaceClass {

	@Id
	@Column(name="PLACE_CLASS_CODE")
	private String placeClassCode;

	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column(name="SIZE_CAPACITY")
	private Integer sizeCapacity;
	
	@Column(name="WEIGHT_CAPACITY")
	private Integer weightCapacity;

	@OneToMany(mappedBy="id.placeClassCode", fetch=FetchType.EAGER)
	private Set<PlaceClassAttr> attrModifiers;
	
	@OneToMany(mappedBy="id.placeClassCode", fetch=FetchType.EAGER)
	private Set<PlaceClassSkill> skillModifiers;
	
	@Column(name="PARENT_CLASS_CODE")
	private String parentClassCode;

	@Column(name="BUILD_COST")	
	private Integer buildCost;
	
	@Column(name="BUILD_EFFORT")
	private Integer buildEffort;
	
	@Column(name="MATERIAL_CODE")
	private String material;

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

	public Integer getSizeCapacity() {
		return sizeCapacity;
	}

	public void setSizeCapacity(Integer sizeCapacity) {
		this.sizeCapacity = sizeCapacity;
	}

	public Integer getWeightCapacity() {
		return weightCapacity;
	}

	public void setWeightCapacity(Integer weightCapacity) {
		this.weightCapacity = weightCapacity;
	}

	public String getPlaceClassCode() {
		return placeClassCode;
	}

	public void setPlaceClassCode(String placeClassCode) {
		this.placeClassCode = placeClassCode;
	}

	public String getParentClassCode() {
		return parentClassCode;
	}

	public void setParentClassCode(String parentClassCode) {
		this.parentClassCode = parentClassCode;
	}

	public Integer getBuildCost() {
		return buildCost;
	}

	public void setBuildCost(Integer buildCost) {
		this.buildCost = buildCost;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getBuildEffort() {
		return buildEffort;
	}

	public void setBuildEffort(Integer buildEffort) {
		this.buildEffort = buildEffort;
	}
	
}
