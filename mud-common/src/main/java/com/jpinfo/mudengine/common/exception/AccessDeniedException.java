package com.jpinfo.mudengine.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AccessDeniedException(String errorMessage) {
		super(errorMessage);
	}

}
