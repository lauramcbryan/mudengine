package com.jpinfo.mudengine.item.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

import java.util.*;


/**
 * The persistent class for the mud_item database table.
 * 
 */
@Entity
@Table(name="mud_item")
@SequenceGenerator(name = "mud_item_seq", sequenceName="mud_item_seq", allocationSize=1)
@Data
public class MudItem implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="mud_item_seq", strategy=GenerationType.SEQUENCE)
	private Long code;
	
	private String name;

	@ManyToOne
	@JoinColumn(name="class_code", referencedColumnName="CODE")
	private MudItemClass itemClass;
	
	@Column(name="current_world")
	private String curWorld;
	
	@Column(name="current_place")
	private Integer curPlaceCode;
	
	@Column(name="current_owner")
	private Long curOwner;
	
	private Integer quantity;
	

	//bi-directional many-to-one association to ItemAttr
	@OneToMany(mappedBy="id.code", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<MudItemAttr> attrs;

	public MudItem() {
		this.attrs = new HashSet<>();
	}

}