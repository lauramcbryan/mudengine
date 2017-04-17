package com.jpinfo.mudengine.being.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the mud_skill database table.
 * 
 */
@Entity
@Table(name="mud_skill")
public class MudSkill implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((skillCode == null) ? 0 : skillCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MudSkill other = (MudSkill) obj;
		if (skillCode == null) {
			if (other.skillCode != null)
				return false;
		} else if (!skillCode.equals(other.skillCode))
			return false;
		return true;
	}

	@Id
	@Column(name="skill_code")
	private String skillCode;

	private String description;

	private String name;

	//bi-directional many-to-one association to MudSkillCategory
	@ManyToOne
	@JoinColumn(name="category_code")
	private MudSkillCategory category;

	public MudSkill() {
	}

	public String getSkillCode() {
		return this.skillCode;
	}

	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MudSkillCategory getCategory() {
		return this.category;
	}

	public void setCategory(MudSkillCategory category) {
		this.category = category;
	}

}