package com.jpinfo.mudengine.common.being;

import java.io.Serializable;

import lombok.Data;


/**
 * The persistent class for the mud_being_items database table.
 * 
 */
@Data
public class BeingItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer itemCode;

	private Integer qtty;

	private Integer usageCount;
}