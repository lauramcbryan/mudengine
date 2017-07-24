package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mud_being_class database table.
 * 
 */
@Entity
@Table(name="mud_being_class")
public class MudBeingClass implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="being_class")
	private String beingClass;

	private String description;

	private String name;

	private Integer size;

	@Column(name="weight_capacity")
	private Integer weightCapacity;

	//bi-directional many-to-one association to MudBeingClassAttr
	@OneToMany(mappedBy="id.beingClass", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingClassAttr> attributes;

	//bi-directional many-to-one association to MudBeingClassSkill
	@OneToMany(mappedBy="id.beingClass", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingClassSkill> skills;
	
	@OneToMany(mappedBy="id.beingClassCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingClassSlot> slots;

	public MudBeingClass() {
	}

	public String getBeingClass() {
		return this.beingClass;
	}

	public void setBeingClass(String beingClass) {
		this.beingClass = beingClass;
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

	public Integer getSize() {
		return this.size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getWeightCapacity() {
		return this.weightCapacity;
	}

	public void setWeightCapacity(Integer weightCapacity) {
		this.weightCapacity = weightCapacity;
	}

	public List<MudBeingClassAttr> getAttributes() {
		return this.attributes;
	}

	public List<MudBeingClassSkill> getSkills() {
		return this.skills;
	}

	public List<MudBeingClassSlot> getSlots() {
		return slots;
	}
	
	
}