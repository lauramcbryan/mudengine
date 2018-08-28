package com.jpinfo.mudengine.common.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientResponseException;

import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.GeneralException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;

public abstract class BaseServiceClient {
	

	protected HttpEntity<Object> getEmptyHttpEntity() {
		
		String authToken = (String)SecurityContextHolder.getContext().getAuthentication().getCredentials();
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(CommonConstants.AUTH_TOKEN_HEADER, authToken);
		
		return new HttpEntity<>(authHeaders);
	}
	
	
	protected void handleError(RestClientResponseException exception) {

		try {
			ApiErrorMessage restError = ApiErrorMessage.build(exception.getResponseBodyAsString());
			
			switch(exception.getRawStatusCode()) {
			
				case 400:
					throw new IllegalParameterException(restError.getMessage());
				case 403:
					throw new AccessDeniedException(restError.getMessage());
				case 404:
					throw new EntityNotFoundException(restError.getMessage());
				default:
					throw new GeneralException(LocalizedMessages.API_ERROR_MESSAGE);
			}
		} catch(IllegalParameterException | AccessDeniedException | EntityNotFoundException | GeneralException e) {
			throw e;
		}catch(Exception e) {
		
			throw new GeneralException(LocalizedMessages.API_ERROR_MESSAGE);
		}
	}

}
