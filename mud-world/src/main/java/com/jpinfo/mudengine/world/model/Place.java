package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="MUD_PLACE")
public class Place {
	
	@Id
	@Column(name="PLACE_CODE")
	private Integer placeCode;

	@Column
	private String name;
	

	@Column
	private String description;
	
	@ManyToOne
	@JoinColumn(name="TERRAIN_CODE", referencedColumnName="TERRAIN_CODE", insertable=false, updatable=false)
	private Terrain terrain;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceBeings> beings;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceItems> items;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER)
	private Set<PlaceExits> exits;

	
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

	public Terrain getTerrain() {
		return terrain;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
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
