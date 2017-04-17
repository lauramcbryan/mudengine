package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;


/**
 * The persistent class for the mud_being_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_skill_modifier")
public class MudBeingSkillModifier implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingSkillModifierPK id;

	@Column(name="skill_offset")
	private float skillOffset;
	
	@Column(name="end_turn")
	private Integer endTurn;
	

	public MudBeingSkillModifier() {
	}

	public MudBeingSkillModifierPK getId() {
		return this.id;
	}

	public void setId(MudBeingSkillModifierPK id) {
		this.id = id;
	}

	public float getOffset() {
		return this.skillOffset;
	}

	public void setOffset(float skillOffset) {
		this.skillOffset = skillOffset;
	}

	public Integer getEndTurn() {
		return endTurn;
	}

	public void setEndTurn(Integer endTurn) {
		this.endTurn = endTurn;
	}

	
}