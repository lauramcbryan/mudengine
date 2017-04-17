package com.jpinfo.mudengine.common.being;

public class BeingSkillModifier {

	private String skillCode;
	private String originCode;
	private String originType;

	private Float offset;
	private Integer endTurn;
	
	public BeingSkillModifier() {
		
	}

	public String getSkillCode() {
		return skillCode;
	}

	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

	public String getOriginCode() {
		return originCode;
	}

	public void setOriginCode(String originCode) {
		this.originCode = originCode;
	}

	public String getOriginType() {
		return originType;
	}

	public void setOriginType(String originType) {
		this.originType = originType;
	}

	public Float getOffset() {
		return offset;
	}

	public void setOffset(Float offset) {
		this.offset = offset;
	}

	public Integer getEndTurn() {
		return endTurn;
	}

	public void setEndTurn(Integer endTurn) {
		this.endTurn = endTurn;
	}

	
}
