package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The primary key class for the mud_being_class_skills database table.
 * 
 */
@Embeddable
@Data
@EqualsAndHashCode
public class BeingClassSkillPK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_class_code", insertable=false, updatable=false)
	private String beingClass;

	@Column(name="skill_code", insertable=false, updatable=false)
	private String skillCode;
}