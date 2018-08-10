package com.jpinfo.mudengine.being.model.pk;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The primary key class for the mud_being_class_attr database table.
 * 
 */
@Embeddable
@Data
@EqualsAndHashCode
public class BeingClassAttrPK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="class_code", insertable=false, updatable=false)
	private String classCode;

	@Column(name="code", insertable=false, updatable=false)
	private String code;
}