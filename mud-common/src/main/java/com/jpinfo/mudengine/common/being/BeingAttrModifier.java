package com.jpinfo.mudengine.common.being;

public class BeingAttrModifier {

	private String attribute;
	private String originCode;
	private String originType;
	
	private float offset;
	private Integer endTurn;
	
	public BeingAttrModifier() {
		
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
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

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public Integer getEndTurn() {
		return endTurn;
	}

	public void setEndTurn(Integer endTurn) {
		this.endTurn = endTurn;
	}
	
	
}
