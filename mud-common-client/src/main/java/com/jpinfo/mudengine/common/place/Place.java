package com.jpinfo.mudengine.common.place;

import java.util.*;

import com.jpinfo.mudengine.common.placeclass.PlaceClass;

import lombok.Data;

@Data
public class Place {
	
	private Integer code;
	
	private String name;

	private String classCode;
	
	private PlaceClass placeClass;
	
	private Map<String, PlaceExit> exits;
	
	private Map<String, Integer> attrs;
	
	public Place() {
		this.attrs = new HashMap<>();
		this.exits = new HashMap<>();
	}
}
