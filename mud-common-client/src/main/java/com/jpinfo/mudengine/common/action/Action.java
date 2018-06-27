package com.jpinfo.mudengine.common.action;

import lombok.Data;

@Data
public class Action {
	
	public enum EnumActionState {NOT_STARTED, STARTED, COMPLETED, CANCELLED, REFUSED};
	public enum EnumTargetType {BEING, ITEM, PLACE, DIRECTION};
	
	private Long issuerCode;
	
	private Long actorCode;

	private Integer actionClassCode;
	
	private String mediatorCode;

	private EnumTargetType mediatorType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}

	private String targetCode;

	private EnumTargetType targetType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}
	
	private Long actionId;
	
	private Long startTurn;
	
	private Long endTurn;
	
	private EnumActionState curState;
	
	public Action() {
		this.curState = EnumActionState.NOT_STARTED;
	}
}
