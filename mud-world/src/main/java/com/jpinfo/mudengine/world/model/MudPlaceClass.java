package com.jpinfo.mudengine.world.model;

import java.util.Set;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="MUD_PLACE_CLASS")
@Data
public class MudPlaceClass {

	@Id
	@Column(name="PLACE_CLASS_CODE", length = 20)
	private String placeClassCode;

	@Column(nullable = false, length = 30)
	private String name;
	
	@Column(length = 500)
	private String description;
	
	@Column(name="SIZE_CAPACITY")
	private Integer sizeCapacity;
	
	@Column(name="WEIGHT_CAPACITY")
	private Integer weightCapacity;

	@OneToMany(mappedBy="id.placeClassCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceClassAttr> attrs;
	
	@Column(name="PARENT_CLASS_CODE", length = 20)
	private String parentClassCode;

	@Column(name="BUILD_COST")	
	private Integer buildCost;
	
	@Column(name="BUILD_EFFORT")
	private Integer buildEffort;
	
	@Column(name="DEMISE_CLASS_CODE", length = 20)
	private String demisePlaceClassCode;
}
