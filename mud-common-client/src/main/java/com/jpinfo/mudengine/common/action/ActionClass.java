package com.jpinfo.mudengine.common.action;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ActionClass {
	
	public static final int ACTION_CLASS_SIMPLE = 0;
	public static final int ACTION_CLASS_CONTINUOUS = 1;

	private Integer actionClassCode;
	
	private String verb;
	
	private Integer actionType;    // 0 = SIMPLE, 1 = CONTINUOUS (effects every turn)
	
	private String successRateExpr;	

	private String nroTurnsExpr;	
	
	private SortedSet<ActionClassPrereq> prereqList;
	
	private SortedSet<ActionClassEffect> effectList;
	
	public ActionClass() {
		
		this.prereqList = new TreeSet<ActionClassPrereq>();
		this.effectList = new TreeSet<ActionClassEffect>();
	}

	public Integer getActionClassCode() {
		return actionClassCode;
	}

	public void setActionClassCode(Integer actionClassCode) {
		this.actionClassCode = actionClassCode;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
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

	public Set<ActionClassPrereq> getPrereqList() {
		return prereqList;
	}

	public void setPrereqList(SortedSet<ActionClassPrereq> prereqList) {
		this.prereqList = prereqList;
	}

	public Set<ActionClassEffect> getEffectList() {
		return effectList;
	}

	public void setEffectList(SortedSet<ActionClassEffect> effectList) {
		this.effectList = effectList;
	}

	
}
