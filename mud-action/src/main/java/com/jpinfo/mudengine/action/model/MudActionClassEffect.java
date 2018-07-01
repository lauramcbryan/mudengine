package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="MUD_ACTION_CLASS_EFFECT")
@Data
@EqualsAndHashCode(of= {"actionClassCode", "evalOrder"})
public class MudActionClassEffect implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	

	@Id
	@Column(name="ACTION_CLASS_CODE")
	private Integer actionClassCode;
	
	@Id
	@Column
	private Integer evalOrder;
	
	@Column(name="EFFECT_EXPRESSION")
	private String expression;
	
	@Column
	private String messageExpression;

}
