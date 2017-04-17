package com.jpinfo.mudengine.common.command;

public class Command {

	private Long actionId;
	
	private Integer actorCode;	
	
	private Integer issuerCode;
	
	private String worldName;
	
	private String actionCode;
	
	private Integer mediatorCode;
	
	private Integer placeCode;
	
	private String targetCode;

	private String targetType;  // {BEING, ITEM, PLACE, PLACE_CLASS}
		
	
	public Command() {
		
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public Integer getIssuerCode() {
		return issuerCode;
	}

	public void setIssuerCode(Integer issuerCode) {
		this.issuerCode = issuerCode;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Integer getActorCode() {
		return actorCode;
	}

	public void setActorCode(Integer actorCode) {
		this.actorCode = actorCode;
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
