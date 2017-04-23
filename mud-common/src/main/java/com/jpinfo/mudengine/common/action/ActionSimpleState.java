package com.jpinfo.mudengine.common.action;


public class ActionSimpleState {

	private Long actionId;
	
	private Integer startTurn;
	
	private Integer endTurn;
	
	private Integer curState;
	
	public ActionSimpleState() {
		
	}

	public Integer getStartTurn() {
		return startTurn;
	}

	public void setStartTurn(Integer startTurn) {
		this.startTurn = startTurn;
	}

	public Integer getEndTurn() {
		return endTurn;
	}

	public void setEndTurn(Integer endTurn) {
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
