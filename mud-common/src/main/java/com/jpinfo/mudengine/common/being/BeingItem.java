package com.jpinfo.mudengine.common.being;

import java.io.Serializable;


/**
 * The persistent class for the mud_being_items database table.
 * 
 */
public class BeingItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer itemCode;

	private Integer qtty;

	private Integer usageCount;

	public BeingItem() {
	}

	public Integer getItemCode() {
		return itemCode;
	}

	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getQtty() {
		return qtty;
	}

	public void setQtty(Integer qtty) {
		this.qtty = qtty;
	}

	public Integer getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}
	
	
}