package com.jpinfo.mudengine.common.action;

import lombok.Data;

@Data
public class ActionClassEffect implements Comparable<ActionClassEffect> {

	private Integer evalOrder;
	
	private String expression;

	private String messageExpression;
	
	public int compareTo(ActionClassEffect other) {
		return this.evalOrder.compareTo(other.evalOrder);
	}
}
