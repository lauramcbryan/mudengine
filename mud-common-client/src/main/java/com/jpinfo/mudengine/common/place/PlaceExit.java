package com.jpinfo.mudengine.common.place;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class PlaceExit {
	
	protected static final List<String> DIRECTIONS = Arrays.asList("EAST", "NORTH", "UP", "DOWN", "SOUTH", "WEST");

	private String name;
	
	private boolean opened;
	
	private boolean visible;
	
	private boolean locked;
	
	private boolean lockable;

	private Integer targetPlaceCode;
	
	
	public static String getOpposedDirection(String direction) {
		
		int size = PlaceExit.DIRECTIONS.size(); 
		int originalPos = PlaceExit.DIRECTIONS.indexOf(direction);
		
		int newpos = size - originalPos - 1;
		
		return PlaceExit.DIRECTIONS.get(newpos);
	}
}
