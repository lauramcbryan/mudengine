package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the mud_being database table.
 * 
 */
@Entity
@Table(name="mud_being")
public class Being implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="being_code")
	private Integer beingCode;

	@Column(name="being_class")
	private String beingClass;

	@OneToMany(mappedBy="id.beingCode")
	private List<BeingAttr> attributes;

	@OneToMany(mappedBy="id.beingCode")
	private List<BeingItem> items;

	@OneToMany(mappedBy="id.beingCode")
	private List<BeingSkill> skills;
	
	@Column(name="last_world")
	private String lastWorld;
	
	@Column(name="player_id")
	private Integer playerId;

	public Being() {
	}

	public Integer getBeingCode() {
		return this.beingCode;
	}

	public void setBeingCode(Integer beingCode) {
		this.beingCode = beingCode;
	}

	public String getBeingClass() {
		return this.beingClass;
	}

	public void setMudBeingClass(String beingClass) {
		this.beingClass = beingClass;
	}
	
	public String getLastWorld() {
		return lastWorld;
	}

	public void setLastWorld(String lastWorld) {
		this.lastWorld = lastWorld;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	public List<BeingAttr> getAttributes() {
		return this.attributes;
	}

	public List<BeingItem> getItems() {
		return this.items;
	}

	public List<BeingSkill> getSkills() {
		return this.skills;
	}
}