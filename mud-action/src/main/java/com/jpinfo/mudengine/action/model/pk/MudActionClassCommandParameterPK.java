package com.jpinfo.mudengine.action.model.pk;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MudActionClassCommandParameterPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="COMMAND_ID")
	private Integer commandId;
	
	private String name;
}
