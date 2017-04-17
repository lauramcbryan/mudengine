package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingItemPK;


/**
 * The persistent class for the mud_being_items database table.
 * 
 */
@Entity
@Table(name="mud_being_item")
public class MudBeingItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingItemPK id;

	private Integer qtty;

	@Column(name="usage_count")
	private Integer usageCount;

	public MudBeingItem() {
	}

	public MudBeingItemPK getId() {
		return this.id;
	}

	public void setId(MudBeingItemPK id) {
		this.id = id;
	}

	public Integer getQtty() {
		return this.qtty;
	}

	public void setQtty(Integer qtty) {
		this.qtty = qtty;
	}

	public Integer getUsageCount() {
		return this.usageCount;
	}

	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
}