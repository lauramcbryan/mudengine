package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="MUD_PLACE")
public class Place {
	
	@Id
	@Column(name="PLACE_CODE")
	private Integer placeCode;

	@ManyToOne
	@JoinColumn(name="PLACE_CLASS_CODE", referencedColumnName="PLACE_CLASS_CODE", insertable=false, updatable=false)
	private PlaceClass placeClass;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceBeings> beings;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceItems> items;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceExits> exits;

	public PlaceClass getPlaceClass() {
		return placeClass;
	}

	public void setPlaceClass(PlaceClass placeClass) {
		this.placeClass = placeClass;
	}

	public Set<PlaceBeings> getBeings() {
		return beings;
	}

	public void setBeings(Set<PlaceBeings> beings) {
		this.beings = beings;
	}

	public Set<PlaceItems> getItems() {
		return items;
	}

	public void setItems(Set<PlaceItems> items) {
		this.items = items;
	}

	public Set<PlaceExits> getExits() {
		return exits;
	}

	public void setExits(Set<PlaceExits> exits) {
		this.exits = exits;
	}
}
