package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;


/**
 * The persistent class for the mud_item database table.
 * 
 */
@Entity
@Table(name="mud_item")
@SequenceGenerator(name = "mud_item_seq", sequenceName="mud_item_seq", allocationSize=1)
public class MudItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="mud_item_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="item_code")
	private Long itemCode;

	@ManyToOne
	@JoinColumn(name="item_class", referencedColumnName="item_class")
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
		this.attrs = new ArrayList<MudItemAttr>();
	}

	public Long getItemCode() {
		return this.itemCode;
	}

	public void setItemCode(Long itemCode) {
		this.itemCode = itemCode;
	}

	public MudItemClass getItemClass() {
		return itemClass;
	}

	public void setItemClass(MudItemClass itemClass) {
		this.itemClass = itemClass;
	}

	public List<MudItemAttr> getAttrs() {
		return this.attrs;
	}

	public String getCurWorld() {
		return curWorld;
	}

	public void setCurWorld(String curWorld) {
		this.curWorld = curWorld;
	}

	public Integer getCurPlaceCode() {
		return curPlaceCode;
	}

	public void setCurPlaceCode(Integer curPlaceCode) {
		this.curPlaceCode = curPlaceCode;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Long getCurOwner() {
		return curOwner;
	}

	public void setCurOwner(Long currentOwner) {
		this.curOwner = currentOwner;
	}

}