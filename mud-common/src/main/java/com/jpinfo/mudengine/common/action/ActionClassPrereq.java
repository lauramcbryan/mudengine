package com.jpinfo.mudengine.common.action;


public class ActionClassPrereq implements Comparable<ActionClassPrereq> {

	private Integer evalOrder;
	
	private String checkExpression;
	
	private String failExpression;

	public Integer getEvalOrder() {
		return evalOrder;
	}

	public void setEvalOrder(Integer evalOrder) {
		this.evalOrder = evalOrder;
	}

	public String getCheckExpression() {
		return checkExpression;
	}

	public void setCheckExpression(String checkExpression) {
		this.checkExpression = checkExpression;
	}

	public String getFailExpression() {
		return failExpression;
	}

	public void setFailExpression(String failExpression) {
		this.failExpression = failExpression;
	}

	public int compareTo(ActionClassPrereq other) {
		return this.evalOrder.compareTo(other.evalOrder);
	}
	
	

}
