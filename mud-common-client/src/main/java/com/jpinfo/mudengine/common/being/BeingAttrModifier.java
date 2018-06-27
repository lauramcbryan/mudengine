package com.jpinfo.mudengine.common.being;

import lombok.Data;

@Data
public class BeingAttrModifier {

	private String attribute;
	private String originCode;
	private String originType;
	
	private float offset;
	private Integer endTurn;
}
