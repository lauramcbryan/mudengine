package com.jpinfo.mudengine.common.action;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Data;

@Data
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
}
