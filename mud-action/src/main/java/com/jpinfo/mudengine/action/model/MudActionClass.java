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
	

	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassPrereq> prereqList;
	
	@OneToMany(mappedBy="actionClassCode")
	private Set<MudActionClassCost> costList;
	
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

	public Set<MudActionClassCost> getCostList() {
		return costList;
	}

	public Set<MudActionClassEffect> getEffectList() {
		return effectList;
	}


	public String getVerb() {
		return verb;
	}
}
