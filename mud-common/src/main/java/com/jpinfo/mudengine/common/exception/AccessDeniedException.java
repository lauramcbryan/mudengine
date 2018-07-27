package com.jpinfo.mudengine.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AccessDeniedException(String key, Object... params ) {
		super(LocalizedMessages.getMessage(key, params));
	}

}
