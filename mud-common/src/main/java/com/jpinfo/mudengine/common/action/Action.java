package com.jpinfo.mudengine.common.action;

public class Action {

	private Long issuerCode;
	
	private Long actorCode;

	private String actionCode;
	
	private String worldName;
	
	private Integer mediatorCode;
	
	private Integer placeCode;
	
	private String targetCode;

	private String targetType;  // {BEING, ITEM, PLACE, PLACE_CLASS}
	
	public Action() {
		
	}

	

	public Long getIssuerCode() {
		return issuerCode;
	}



	public void setIssuerCode(Long issuerCode) {
		this.issuerCode = issuerCode;
	}



	public Long getActorCode() {
		return actorCode;
	}



	public void setActorCode(Long actorCode) {
		this.actorCode = actorCode;
	}



	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Integer getMediatorCode() {
		return mediatorCode;
	}

	public void setMediatorCode(Integer mediatorCode) {
		this.mediatorCode = mediatorCode;
	}

	public Integer getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(Integer placeCode) {
		this.placeCode = placeCode;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
