package com.jpinfo.mudengine.client.utils;

import java.io.IOException;
import java.util.Date;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiErrorMessage {
	
	private Date timestamp;
	
	private Integer status;
	
	private String error;
	
	private String message;
	
	private String path;
	
	public static ApiErrorMessage build(String jsonError) throws JsonParseException, IOException {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		ApiErrorMessage result = 
					jsonMapper.readValue(jsonError, 
							new TypeReference<ApiErrorMessage>() {});

		return result;
	}
	
	public ApiErrorMessage() {
		
	}
	

	public Date getTimestamp() {
		return timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public void setError(String error) {
		this.error = error;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public void setPath(String path) {
		this.path = path;
	}

}
