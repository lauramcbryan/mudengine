package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

import com.jpinfo.mudengine.common.action.Action;

@Entity
@Table(name="MUD_ACTION")
@SequenceGenerator(name="MUD_ACTION_SEQ", sequenceName="MUD_ACTION_SEQ", allocationSize=1)
public class MudAction {
	
	@Id
	@Column(name="ACTION_UID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MUD_ACTION_SEQ")
	private Long actionId;
	
	@Column(name="ISSUER_CODE")
	private Long issuerCode;
	
	@Column(name="ACTOR_CODE")
	private Long actorCode;
	

	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
	
	@Column(name="WORLD_NAME")
	private String worldName;
	
	@Column(name="MEDIATOR_CODE")
	private Long mediatorCode;
	
	@Column(name="PLACE_CODE")
	private Integer placeCode;
	
	@Column(name="TARGET_CODE")
	private String targetCode;

	@Column(name="TARGET_TYPE")
	private Integer targetType;  // {BEING, ITEM, PLACE, PLACE_CLASS}
	
	@Column(name="START_TURN")
	private Long startTurn;
	
	@Column(name="END_TURN")
	private Long endTurn;
	
	@Column(name="CUR_STATE")
	private Integer currState;
	
	@Column(name="SUCCESS_RATE")
	private Float successRate;
	
	public MudAction() {
		this.currState = 0;
		
	}

	public String getActionClassCode() {
		return actionClassCode;
	}

	public void setActionClassCode(String actionClassCode) {
		this.actionClassCode = actionClassCode;
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

	public Integer getTargetType() {
		return targetType;
	}
	
	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}
	
	public Action.EnumTargetType getTargetTypeEnum() {
		return Action.EnumTargetType.values()[this.targetType];
	}
	
	public void setTargetType(Action.EnumTargetType enumTargetType) {
		this.targetType = enumTargetType.ordinal();
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
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

	public Integer getCurrState() {
		return currState;
	}

	public void setCurrState(Integer currState) {
		this.currState = currState;
	}
	
	public void setCurrState(Action.EnumActionState enumState) {
		this.currState = enumState.ordinal();
	}
	
	public Action.EnumActionState getCurrStateEnum() {
		return Action.EnumActionState.values()[this.currState];
	}

	public Float getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Float successRate) {
		this.successRate = successRate;
	}
}
