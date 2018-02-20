package com.jpinfo.mudengine.action.interfaces;

import java.util.List;

import com.jpinfo.mudengine.action.utils.ActionMessage;

public interface ActionTarget {

	List<ActionMessage> getMessages();
	
	void addMessage(Long senderCode, String messageKey, String... args);
	
	void describeIt(ActionTarget target);
	
}
