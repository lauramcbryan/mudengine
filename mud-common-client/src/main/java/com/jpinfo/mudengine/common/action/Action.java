package com.jpinfo.mudengine.common.action;

import lombok.Data;

@Data
public class Action {
	
	public enum EnumRunningType {SIMPLE, CONTINUOUS, PROLONGED}
	public enum EnumActionState {NOT_STARTED, STARTED, COMPLETED, CANCELLED, REFUSED}
	public enum EnumTargetType {BEING, ITEM, PLACE, DIRECTION}
	
	private Long issuerCode;
	
	private Long actorCode;

	private String actionClassCode;
	
	private String mediatorCode;

	private EnumTargetType mediatorType;

	private String targetCode;

	private EnumTargetType targetType;
	
	private Long actionId;
	
	private Long startTurn;
	
	private Long endTurn;
	
	private EnumRunningType runType;
	
	private EnumActionState curState;
	
	public Action() {
		this.curState = EnumActionState.NOT_STARTED;
		this.runType = EnumRunningType.SIMPLE;
	}
}
