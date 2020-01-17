package com.jpinfo.mudengine.common.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class CommandParam implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum enumParamTypes {
		ANY_STRING, SECURE_STRING, EMAIL, ANY_NUMBER, // General command parameters 
		BEING_CLASSES, PLAYER_BEINGS, 	// System command parameters
		BEING, ITEM, DIRECTION, PLACE}	// Game command parameters

	private String name;
	private String inputMessage;
	private enumParamTypes type;
	private boolean required;

	private transient Map<String, Object> staticDomainValues;
	
	private String defaultValue;
	

	public CommandParam() {
		this.staticDomainValues = Collections.emptyMap();
	}
	
	public void setStaticDomainValues(List<String> simpleValues) {
		
		this.staticDomainValues =
			simpleValues.stream()
				.collect(Collectors.toMap(
						String::toString, 
						String::toString));
	}
}
