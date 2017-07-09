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
	private Set<MudPlaceExit> exits;
	
	@OneToMany(mappedBy="id.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceAttr> attrs;
	
	
	public MudPlace() {
		this.exits = new HashSet<MudPlaceExit>();
		this.attrs = new HashSet<MudPlaceAttr>();
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

	public Set<MudPlaceExit> getExits() {
		return exits;
	}

	public Set<MudPlaceAttr> getAttrs() {
		return attrs;
	}

	public void setAttrs(Set<MudPlaceAttr> attrs) {
		this.attrs = attrs;
	}
	
	
}
