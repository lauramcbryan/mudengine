package com.jpinfo.mudengine.action.model;

import javax.persistence.*;

@Entity
@Table(name="MUD_ACTION_CLASS_EFFECT")
public class MudActionClassEffect implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	

	@Id
	@Column(name="ACTION_CLASS_CODE")
	private String actionClassCode;
	
	@Id
	@Column
	private Integer evalOrder;
	
	@Column(name="EFFECT_EXPRESSION")
	private String expression;
	
	@Column
	private String messageExpression;

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

	public String getMessageExpression() {
		return messageExpression;
	}

	public void setMessageExpression(String messageExpression) {
		this.messageExpression = messageExpression;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionClassCode == null) ? 0 : actionClassCode.hashCode());
		result = prime * result + ((evalOrder == null) ? 0 : evalOrder.hashCode());
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
		MudActionClassEffect other = (MudActionClassEffect) obj;
		if (actionClassCode == null) {
			if (other.actionClassCode != null)
				return false;
		} else if (!actionClassCode.equals(other.actionClassCode))
			return false;
		if (evalOrder == null) {
			if (other.evalOrder != null)
				return false;
		} else if (!evalOrder.equals(other.evalOrder))
			return false;
		return true;
	}
	

	

}
