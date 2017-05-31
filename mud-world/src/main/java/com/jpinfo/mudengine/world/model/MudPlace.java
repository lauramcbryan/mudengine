package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="MUD_PLACE")
public class MudPlace {
	
	@Id
	@Column(name="PLACE_CODE")
	private Integer placeCode;

	@ManyToOne
	@JoinColumn(name="PLACE_CLASS_CODE", referencedColumnName="PLACE_CLASS_CODE")
	private MudPlaceClass placeClass;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceBeings> beings;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceItems> items;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceExits> exits;
	
	public MudPlace() {
		this.beings = new HashSet<MudPlaceBeings>();
		this.items = new HashSet<MudPlaceItems>();
		this.exits = new HashSet<MudPlaceExits>();
	}

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public MudPlaceClass getPlaceClass() {
		return placeClass;
	}

	public void setPlaceClass(MudPlaceClass placeClass) {
		this.placeClass = placeClass;
	}

	public Set<MudPlaceBeings> getBeings() {
		return beings;
	}

	public Set<MudPlaceItems> getItems() {
		return items;
	}

	public Set<MudPlaceExits> getExits() {
		return exits;
	}
}
