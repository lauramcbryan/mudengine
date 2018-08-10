package com.jpinfo.mudengine.being.model.pk;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode
public class MudBeingClassSlotPK implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String classCode;
	
	private String code;
}
