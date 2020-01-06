package com.jpinfo.mudengine.being;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;

public class BeingTestData {
	
	private static final String MUD_BEING_PREFIX = "src/test/resources/mudbeing-";
	private static final String MUD_BEING_SUFFIX = ".json";
	
	private static final String MUD_BEING_CLASS_PREFIX = "src/test/resources/mudbeingclass-";
	private static final String MUD_BEING_CLASS_SUFFIX = ".json";
	
	public static final Long READ_BEING_ID = 1L;
	
	public static final String MUD_ORIGINAL_BEING_CLASS = "TEST";
	public static final String MUD_CHANGED_BEING_CLASS = "CHANGED";
	
	
	private static ObjectMapper jsonMapper = new ObjectMapper();
	
	public static MudBeingClass loadMudBeingClass(String className) throws IOException{
		
		return jsonMapper.readValue(new File(
				BeingTestData.MUD_BEING_CLASS_PREFIX +
				className + 
				BeingTestData.MUD_BEING_CLASS_SUFFIX
				), MudBeingClass.class);
	}
	
	public static MudBeing loadMudBeing(Long beingCode) throws IOException{
		
		return jsonMapper.readValue(new File(
				BeingTestData.MUD_BEING_PREFIX +
				beingCode +
				BeingTestData.MUD_BEING_SUFFIX
				), MudBeing.class);
	}
}
