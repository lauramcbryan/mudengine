package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="MUD_ACTION_CLASS_PREREQ")
@Data
@EqualsAndHashCode(of= {"actionClassCode", "evalOrder"})
public class MudActionClassPrereq implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
	
	@Id
	@Column
	private Integer evalOrder;
	
	@Column(name="CHECK_EXPRESSION")
	private String checkExpression;
	
	@Column(name="FAIL_EXPRESSION")
	private String failExpression;

}
