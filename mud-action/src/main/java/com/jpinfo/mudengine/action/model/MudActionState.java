package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

@Entity
@Table(name="MUD_ACTION_STATE")
public class MudActionState {
	
	@Id
	@Column(name="ACTION_UID")
	private Long actionId;
	
	@Column(name="START_TURN")
	private Integer startTurn;
	
	@Column(name="END_TURN")
	private Integer endTurn;
	
	@Column(name="CUR_STATE")
	private Integer currState;
	
	@Column(name="SUCCESS_RATE")
	private Float successRate;
	
	@OneToOne
	@JoinColumn(name="ACTION_UID", referencedColumnName="ACTION_UID", insertable=false, updatable=false)
	private MudAction action;
	
	public MudActionState() {
		
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
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

	public Integer getCurrState() {
		return currState;
	}

	public void setCurrState(Integer currState) {
		this.currState = currState;
	}

	public Float getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Float successRate) {
		this.successRate = successRate;
	}

	public MudAction getAction() {
		return action;
	}

	public void setAction(MudAction action) {
		this.action = action;
	}
	
}
