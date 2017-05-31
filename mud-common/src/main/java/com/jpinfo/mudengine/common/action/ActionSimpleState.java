package com.jpinfo.mudengine.common.action;


public class ActionSimpleState {
	
	public static final int NOT_STARTED = 0;
	public static final int STARTED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELLED = 3;
	public static final int REFUSED = 4;

	private Long actionId;
	
	private Long startTurn;
	
	private Long endTurn;
	
	private Integer curState;
	
	public ActionSimpleState() {
		
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

	public Integer getCurState() {
		return curState;
	}

	public void setCurState(Integer curState) {
		this.curState = curState;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}
	
	
	
	
}
