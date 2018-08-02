package com.jpinfo.mudengine.action.model;

import java.util.Set;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="MUD_ACTION_CLASS")
@Data
public class MudActionClass {

	@Id
	@Column(name="ACTION_CLASS_CODE")
	private Integer actionClassCode;
	
	@Column(name="ACTION_TYPE")
	private Integer actionType;    /* 0 = SIMPLE, 1 = CONTINUOUS (effects every turn)  */
	
	@Column(name="MEDIATOR_TYPE")
	private String mediatorType;
	
	@Column(name="TARGET_TYPE")
	private String targetType;	
	
	
	@Column(name="SUCCESS_RATE_EXPRESSION")
	private String successRateExpr;	

	@Column(name="NRO_TURNS_EXPRESSION")
	private String nroTurnsExpr;	
	
	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassPrereq> prereqList;
	
	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassEffect> effectList;
}
