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
	@Column(name="item_code")
	private Long itemCode;
	
	@Column(name="name")
	private String itemName;

	@ManyToOne
	@JoinColumn(name="item_class_code", referencedColumnName="ITEM_CLASS_CODE")
	private MudItemClass itemClass;
	
	@Column(name="current_world")
	private String curWorld;
	
	@Column(name="current_place")
	private Integer curPlaceCode;
	
	@Column(name="current_owner")
	private Long curOwner;
	
	private Integer quantity;
	

	//bi-directional many-to-one association to ItemAttr
	@OneToMany(mappedBy="id.itemCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudItemAttr> attrs;

	public MudItem() {
		this.attrs = new ArrayList<>();
	}
}