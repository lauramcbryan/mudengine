package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;

import lombok.Data;


/**
 * The persistent class for the mud_being_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_skill_modifier")
@Data
public class MudBeingSkillModifier implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingSkillModifierPK id;

	@Column(name="skill_offset")
	private float offset;
	
	@Column(name="end_turn")
	private Integer endTurn;
}