package com.jpinfo.mudengine.common.action;


public class ActionClassEffect implements Comparable<ActionClassEffect> {

	private Integer evalOrder;
	
	private String expression;

	private String messageExpression;
	
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

	public int compareTo(ActionClassEffect other) {
		return this.evalOrder.compareTo(other.evalOrder);
	}
	
	

}
