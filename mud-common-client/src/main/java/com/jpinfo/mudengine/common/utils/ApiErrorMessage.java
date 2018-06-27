package com.jpinfo.mudengine.common.utils;

import java.io.IOException;
import java.util.Date;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
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
}
