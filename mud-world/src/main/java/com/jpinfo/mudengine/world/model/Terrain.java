package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

@Entity
@Table(name="MUD_TERRAIN")
public class Terrain {

	@Id
	@Column(name="TERRAIN_CODE")
	private Integer terrainCode;

	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column(name="SIZE_CAPACITY")
	private Integer sizeCapacity;
	
	@Column(name="WEIGHT_CAPACITY")
	private Integer weightCapacity;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="CATEGORY_CODE", referencedColumnName="CATEGORY_CODE", insertable=false, updatable=false)
	private TerrainCategory category;

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

	public TerrainCategory getCategory() {
		return category;
	}

	public void setCategory(TerrainCategory category) {
		this.category = category;
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
	
}
