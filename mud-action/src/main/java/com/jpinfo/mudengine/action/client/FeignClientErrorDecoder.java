package com.jpinfo.mudengine.action.client;

import java.io.BufferedReader;

import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.utils.ApiErrorMessage;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignClientErrorDecoder implements ErrorDecoder {

	private final ErrorDecoder defaultErrorDecoder = new Default();
	
	@Override
	public Exception decode(String methodKey, Response response) {
		
		try {
			
			// Read the response body
			BufferedReader reader = new BufferedReader(response.body().asReader());
			StringBuilder errorMessage = new StringBuilder(); 
			
			while (reader.ready()) {
				errorMessage.append(reader.readLine());
			}
			
			// Transform into an Rest ErrorMessage
			ApiErrorMessage restError = ApiErrorMessage.build(errorMessage.toString());
			
			switch(response.status()) {
			
				case 400:
					return new IllegalParameterException(restError.getMessage());
				case 403:
					return new AccessDeniedException(restError.getMessage());
				case 404:
					return new EntityNotFoundException(restError.getMessage());
				default:
					// Go ahead and throw the default FeignException
			}

			
		} catch(Exception e) {
			// Go ahead and throw the default FeignException
		}
		
		
		return defaultErrorDecoder.decode(methodKey, response);
	}

}
