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
	@Column(name="ITEM_CLASS_CODE")
	private String itemClassCode;

	private float size;

	private float weight;
	
	private String description;
	
	@Column(name="DEMISE_CLASS_CODE", length = 20)
	private String demiseItemClassCode;

	//bi-directional many-to-one association to ItemClassAttr
	@OneToMany(mappedBy="id.itemClassCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudItemClassAttr> attrs;

	public MudItemClass() {
		this.attrs = new ArrayList<>();
	}
}