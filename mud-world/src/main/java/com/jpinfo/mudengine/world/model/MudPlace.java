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
	@JoinColumn(name="PLACE_CLASS_CODE", referencedColumnName="PLACE_CLASS_CODE", insertable=false, updatable=false)
	private MudPlaceClass placeClass;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<MudPlaceBeings> beings;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<MudPlaceItems> items;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<MudPlaceExits> exits;

	public MudPlaceClass getPlaceClass() {
		return placeClass;
	}

	public void setPlaceClass(MudPlaceClass placeClass) {
		this.placeClass = placeClass;
	}

	public Set<MudPlaceBeings> getBeings() {
		return beings;
	}

	public void setBeings(Set<MudPlaceBeings> beings) {
		this.beings = beings;
	}

	public Set<MudPlaceItems> getItems() {
		return items;
	}

	public void setItems(Set<MudPlaceItems> items) {
		this.items = items;
	}

	public Set<MudPlaceExits> getExits() {
		return exits;
	}

	public void setExits(Set<MudPlaceExits> exits) {
		this.exits = exits;
	}
}
