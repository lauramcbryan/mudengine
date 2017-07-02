package com.jpinfo.mudengine.common.interfaces;

public interface Reaction {
	
	String getPrereq();
	
	String getExpression();
	
	Integer getUsageLimit();
	
	Integer getTimeLimit();
}
