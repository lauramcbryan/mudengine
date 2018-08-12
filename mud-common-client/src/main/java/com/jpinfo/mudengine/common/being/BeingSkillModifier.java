package com.jpinfo.mudengine.common.being;

import lombok.Data;

@Data
public class BeingSkillModifier {

	private String code;
	private String originCode;
	private String originType;

	private double offset;
	private Integer endTurn;
}
