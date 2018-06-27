package com.jpinfo.mudengine.common.action;

import lombok.Data;

@Data
public class ActionClassPrereq implements Comparable<ActionClassPrereq> {

	private Integer evalOrder;
	
	private String checkExpression;
	
	private String failExpression;

	public int compareTo(ActionClassPrereq other) {
		return this.evalOrder.compareTo(other.evalOrder);
	}
}
