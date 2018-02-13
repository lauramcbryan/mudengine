package com.jpinfo.mudengine.common.action;

public class Action {
	
	public enum EnumActionState {NOT_STARTED, STARTED, COMPLETED, CANCELLED, REFUSED};
	public enum EnumTargetType {BEING, ITEM, PLACE, DIRECTION};
	
	private Long issuerCode;
	
	private Long actorCode;

	private String actionClassCode;
	
	private String worldName;
	
	private Long mediatorCode;
	
	private Integer placeCode;
	
	private String targetCode;

	private EnumTargetType targetType;  // {BEING, ITEM, PLACE, PLACE_CLASS}
	
	private Long actionId;
	
	private Long startTurn;
	
	private Long endTurn;
	
	private EnumActionState curState;

	
	
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



	public String getActionClassCode() {
		return actionClassCode;
	}

	public void setActionClassCode(String actionCode) {
		this.actionClassCode = actionCode;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Long getMediatorCode() {
		return mediatorCode;
	}

	public void setMediatorCode(Long mediatorCode) {
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

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}



	public Long getStartTurn() {
		return startTurn;
	}



	public void setStartTurn(Long startTurn) {
		this.startTurn = startTurn;
	}



	public Long getEndTurn() {
		return endTurn;
	}



	public void setEndTurn(Long endTurn) {
		this.endTurn = endTurn;
	}



	public EnumTargetType getTargetType() {
		return targetType;
	}



	public void setTargetType(EnumTargetType targetType) {
		this.targetType = targetType;
	}


	public EnumActionState getCurState() {
		return curState;
	}



	public void setCurState(EnumActionState curState) {
		this.curState = curState;
	}

	
	
}
