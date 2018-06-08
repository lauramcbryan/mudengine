package com.jpinfo.mudengine.action.model.pk;

import java.io.Serializable;

import javax.persistence.Column;

public class MudActionClassCommandParameterPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="COMMAND_ID")
	private Integer commandId;
	
	private String name;
	
	
	public MudActionClassCommandParameterPK() {
	}


	public Integer getCommandId() {
		return commandId;
	}


	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandId == null) ? 0 : commandId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MudActionClassCommandParameterPK other = (MudActionClassCommandParameterPK) obj;
		if (commandId == null) {
			if (other.commandId != null)
				return false;
		} else if (!commandId.equals(other.commandId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
