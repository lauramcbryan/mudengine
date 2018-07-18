package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the mud_being database table.
 * 
 */
@Entity
@Table(name="mud_being")
@SequenceGenerator(name = "mud_being_seq", sequenceName="mud_being_seq", allocationSize=1)
@Data
@EqualsAndHashCode(of= {"beingCode"})
public class MudBeing implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="mud_being_seq", strategy=GenerationType.SEQUENCE)
	@Column(name="being_code")
	private Long beingCode;
	
	@ManyToOne
	@JoinColumn(columnDefinition="being_class_code", referencedColumnName="being_class_code")
	private MudBeingClass beingClass;
	
	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingAttr> attrs;

	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingSkill> skills;
	
	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<MudBeingAttrModifier> attrModifiers;
	
	@OneToMany(mappedBy="id.beingCode", cascade=CascadeType.ALL, orphanRemoval=true)
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
	
	@Column(name="being_type")
	private Integer beingType;
	
	private Integer quantity;

	public MudBeing() {
		this.attrs = new ArrayList<>();
		this.skills = new ArrayList<>();
		this.attrModifiers = new ArrayList<>();
		this.skillModifiers = new ArrayList<>();
		this.equipment = new ArrayList<>();
	}
}