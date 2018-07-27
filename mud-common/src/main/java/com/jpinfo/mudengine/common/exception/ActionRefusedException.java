package com.jpinfo.mudengine.common.exception;

import com.jpinfo.mudengine.common.utils.LocalizedMessages;

public class ActionRefusedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ActionRefusedException(String key, Object... params ) {
		super(LocalizedMessages.getMessage(key, params));
	}
}
