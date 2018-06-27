package com.jpinfo.mudengine.world.model;

import javax.persistence.*;

import lombok.Data;

import java.util.*;

@Entity
@Table(name="MUD_PLACE")
@SequenceGenerator(name = "mud_place_seq", sequenceName="mud_place_seq", allocationSize=1)
@Data
public class MudPlace {
	
	@Id
	@GeneratedValue(generator="mud_place_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="PLACE_CODE")
	private Integer placeCode;

	@ManyToOne
	@JoinColumn(name="PLACE_CLASS_CODE", referencedColumnName="PLACE_CLASS_CODE", nullable = false)
	private MudPlaceClass placeClass;
	
	@OneToMany(mappedBy="pk.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceExit> exits;
	
	@OneToMany(mappedBy="id.placeCode", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudPlaceAttr> attrs;
	
	
	public MudPlace() {
		this.exits = new HashSet<MudPlaceExit>();
		this.attrs = new HashSet<MudPlaceAttr>();
	}
}
