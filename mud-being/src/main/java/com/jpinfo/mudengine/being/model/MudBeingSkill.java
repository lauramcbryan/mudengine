package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.MudBeingSkillPK;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the mud_being_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_skill")
@Data
@EqualsAndHashCode(of= {"id"})
public class MudBeingSkill implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MudBeingSkillPK id;

	@Column(name="skill_value")
	private Integer skillValue;
}