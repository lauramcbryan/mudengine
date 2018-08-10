package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The primary key class for the mud_being_skills database table.
 * 
 */
@Embeddable
@Data
@EqualsAndHashCode
public class MudBeingSkillModifierPK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="being_code", insertable=false, updatable=false)
	private Long beingCode;

	@Column(name="code", insertable=false, updatable=false)
	private String code;
	
	@Column(name="origin_code")
	private String originCode;

	@Column(name="origin_type")
	private String originType;
}