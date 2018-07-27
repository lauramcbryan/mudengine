package com.jpinfo.mudengine.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalParameterException(String key, Object...params) {
		super(LocalizedMessages.getMessage(key, params));
	}
}
