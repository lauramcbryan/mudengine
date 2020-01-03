package com.jpinfo.mudengine.world.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="MUD_PLACE_CLASS")
@Data
public class MudPlaceClass {

	@Id
	@Column(name="CODE", length = 20)
	private String code;

	@Column(nullable = false, length = 30)
	private String name;
	
	@Column(length = 50, name="DEFAULT_DESCRIPTION")
	private String description;
	
	@Column(name="SIZE_CAPACITY")
	private Integer sizeCapacity;
	
	@Column(name="WEIGHT_CAPACITY")
	private Integer weightCapacity;

	@OneToMany(mappedBy="id.classCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceClassAttr> attrs = new HashSet<>();
	
	@Column(name="PARENT_CLASS_CODE", length = 20)
	private String parentClassCode;

	@Column(name="BUILD_COST")	
	private Integer buildCost;
	
	@Column(name="BUILD_EFFORT")
	private Integer buildEffort;
	
	@Column(name="DEMISED_CLASS_CODE", length = 20)
	private String demisedPlaceClassCode;
}
