package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * The persistent class for the mud_being_class database table.
 * 
 */
@Entity
@Table(name="mud_being_class")
@Data
@EqualsAndHashCode(of= {"beingClassCode"})
public class MudBeingClass implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="being_class_code")
	private String beingClassCode;

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
}