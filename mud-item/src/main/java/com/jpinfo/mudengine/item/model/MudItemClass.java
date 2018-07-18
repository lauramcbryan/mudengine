package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the mud_item_class database table.
 * 
 */
@Entity
@Table(name="mud_item_class")
@Data
public class MudItemClass implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="item_class")
	private String itemClass;

	private float size;

	private float weight;
	
	private String description;

	//bi-directional many-to-one association to ItemClassAttr
	@OneToMany(mappedBy="id.itemClass", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudItemClassAttr> attrs;

	public MudItemClass() {
		this.attrs = new ArrayList<>();
	}
}