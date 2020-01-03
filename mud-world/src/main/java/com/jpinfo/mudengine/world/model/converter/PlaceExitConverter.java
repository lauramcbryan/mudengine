package com.jpinfo.mudengine.world.model.converter;

import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.world.model.MudPlaceExit;

public class PlaceExitConverter {
	
	public static PlaceExit convert(MudPlaceExit dbPlaceExit) {
		
		PlaceExit result = new PlaceExit();
		
		result.setTargetPlaceCode(dbPlaceExit.getTargetPlaceCode());
		result.setLockable(dbPlaceExit.isLockable());
		result.setLocked(dbPlaceExit.isLocked());
		result.setOpened(dbPlaceExit.isOpened());
		result.setVisible(dbPlaceExit.isVisible());
		
		return result;
	}
}
