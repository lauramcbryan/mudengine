package com.jpinfo.mudengine.action.model;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="MUD_ACTION_CLASS")
public class MudActionClass {

	@Id
	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
	
	@Column
	private String verb;
	
	@Column(name="MEDIATOR_TYPE")
	private String mediatorType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}
	
	@Column(name="TARGET_TYPE")
	private String targetType;  // {BEING, ITEM, PLACE, DIRECTION, MESSAGE}
	
	
	@Column(name="ACTION_TYPE")
	private Integer actionType;    // 0 = SIMPLE, 1 = CONTINUOUS (effects every turn)
	
	@Column(name="SUCCESS_RATE_EXPRESSION")
	private String successRateExpr;	

	@Column(name="NRO_TURNS_EXPRESSION")
	private String nroTurnsExpr;	
	
	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassPrereq> prereqList;
	
	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassEffect> effectList;
	
	public MudActionClass() {
		
	}
	
	public String getActionClassCode() {
		return actionClassCode;
	}

	public void setActionClassCode(String actionClassCode) {
		this.actionClassCode = actionClassCode;
	}

	public Set<MudActionClassPrereq> getPrereqList() {
		return prereqList;
	}

	public Set<MudActionClassEffect> getEffectList() {
		return effectList;
	}


	public String getVerb() {
		return verb;
	}

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}

	public String getSuccessRateExpr() {
		return successRateExpr;
	}

	public void setSuccessRateExpr(String successRateExpr) {
		this.successRateExpr = successRateExpr;
	}

	public String getNroTurnsExpr() {
		return nroTurnsExpr;
	}

	public void setNroTurnsExpr(String nroTurnsExpr) {
		this.nroTurnsExpr = nroTurnsExpr;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getMediatorType() {
		return mediatorType;
	}

	public void setMediatorType(String mediatorType) {
		this.mediatorType = mediatorType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
