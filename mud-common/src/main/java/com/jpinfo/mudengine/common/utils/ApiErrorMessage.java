package com.jpinfo.mudengine.common.utils;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class ApiErrorMessage {
	
	private LocalDateTime timestamp;
	
	private Integer status;
	
	private String error;
	
	private String message;
	
	private String path;
	
	public static ApiErrorMessage build(String jsonError) throws IOException {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		return jsonMapper.readValue(jsonError, 
				new TypeReference<ApiErrorMessage>() {});
	}
}
