package com.jpinfo.mudengine.item.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mud_item database table.
 * 
 */
@Entity
@Table(name="mud_item")
public class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="item_code")
	private Integer itemCode;

	private String description;

	private String name;

	@Column(name="usage_count")
	private Integer usageCount;

	@Column(name="item_class")
	private String itemClass;

	//bi-directional many-to-one association to ItemAttr
	@OneToMany(mappedBy="id.itemCode")
	private List<ItemAttr> attrs;

	//bi-directional many-to-one association to ItemSkill
	@OneToMany(mappedBy="id.itemCode")
	private List<ItemSkill> skills;

	public Item() {
	}

	public Integer getItemCode() {
		return this.itemCode;
	}

	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUsageCount() {
		return this.usageCount;
	}

	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}

	public String getItemClass() {
		return this.itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public List<ItemAttr> getAttrs() {
		return this.attrs;
	}

	public List<ItemSkill> getSkills() {
		return this.skills;
	}

}