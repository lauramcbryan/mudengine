package com.jpinfo.mudengine.common.action;


public class ActionClassPrereq implements Comparable<ActionClassPrereq> {

	private Integer evalOrder;
	
	private String expression;

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

	public int compareTo(ActionClassPrereq other) {
		return this.evalOrder.compareTo(other.evalOrder);
	}
	
	

}
