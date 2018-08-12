package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;


/**
 * The persistent class for the mud_skill_category database table.
 * 
 */
@Entity
@Table(name="mud_skill_category")
@Data
public class MudSkillCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String code;

	private String description;

	private String name;

	@Column(name="attr_code_based")
	private String attrBasedOn;	
}