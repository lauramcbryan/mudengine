package com.jpinfo.mudengine.being.model;

import java.io.Serializable;
import javax.persistence.*;

import com.jpinfo.mudengine.being.model.pk.BeingClassSkillPK;

import lombok.Data;


/**
 * The persistent class for the mud_being_class_skills database table.
 * 
 */
@Entity
@Table(name="mud_being_class_skill")
@Data
public class MudBeingClassSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeingClassSkillPK id;

	@Column(name="skill_value")
	private Integer skillValue;
}