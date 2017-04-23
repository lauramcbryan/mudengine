package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

@Entity
@Table(name="MUD_ACTION_CLASS_PREREQ")
public class MudActionClassPrereq {
	
	@Id
	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
	
	@Id
	@Column
	private Integer evalOrder;
	
	@Column
	private String expression;
	
	@Column
	private Integer messageCode;

	public String getActionClassCode() {
		return actionClassCode;
	}

	public void setActionClassCode(String actionClassCode) {
		this.actionClassCode = actionClassCode;
	}

	public Integer getEvalOrder() {
		return evalOrder;
	}

	public void setEvalOrder(Integer evalOrder) {
		this.evalOrder = evalOrder;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Integer getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(Integer messageCode) {
		this.messageCode = messageCode;
	}
	
	

}
