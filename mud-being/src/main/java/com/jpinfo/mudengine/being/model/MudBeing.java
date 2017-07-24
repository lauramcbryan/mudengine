package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the mud_being database table.
 * 
 */
@Entity
@Table(name="mud_being")
@SequenceGenerator(name = "mud_being_seq", sequenceName="mud_being_seq", allocationSize=1)
public class MudBeing implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="mud_being_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="being_code")
	private Long beingCode;

	@ManyToOne
	@JoinColumn(columnDefinition="being_class", referencedColumnName="being_class")
	private MudBeingClass beingClass;

	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingAttr> attrs;

	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingSkill> skills;
	
	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingAttrModifier> attrModifiers;
	
	@OneToMany(mappedBy="id.beingCoe", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingSlot> equipment;


	@OneToMany(mappedBy="id.beingCode")
	private List<MudBeingSkillModifier> skillModifiers;

	@Column
	private String name;
	
	@Column(name="current_world")
	private String curWorld;
	
	@Column(name="current_place")
	private Integer curPlaceCode;
	
	@Column(name="player_id")
	private Long playerId;

	public MudBeing() {
		this.attrs = new ArrayList<MudBeingAttr>();
		this.skills = new ArrayList<MudBeingSkill>();
		this.attrModifiers = new ArrayList<MudBeingAttrModifier>();
		this.skillModifiers = new ArrayList<MudBeingSkillModifier>();
		this.equipment = new ArrayList<MudBeingSlot>();
	}

	public Long getBeingCode() {
		return this.beingCode;
	}

	public void setBeingCode(Long beingCode) {
		this.beingCode = beingCode;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public List<MudBeingAttr> getAttrs() {
		return this.attrs;
	}

	public List<MudBeingSkill> getSkills() {
		return this.skills;
	}

	public Integer getCurPlaceCode() {
		return curPlaceCode;
	}

	public void setCurPlaceCode(Integer curPlaceCode) {
		this.curPlaceCode = curPlaceCode;
	}

	public String getCurWorld() {
		return curWorld;
	}

	public void setCurWorld(String curWorld) {
		this.curWorld = curWorld;
	}
	
	public MudBeingClass getBeingClass() {
		return beingClass;
	}

	public void setBeingClass(MudBeingClass beingClass) {
		this.beingClass = beingClass;
	}

	public List<MudBeingAttrModifier> getAttrModifiers() {
		return attrModifiers;
	}

	public List<MudBeingSkillModifier> getSkillModifiers() {
		return skillModifiers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MudBeingSlot> getEquipment() {
		return equipment;
	}
	
	
}