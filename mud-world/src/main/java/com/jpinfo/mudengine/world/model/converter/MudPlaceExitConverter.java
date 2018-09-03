package com.jpinfo.mudengine.world.model.converter;

import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.MudPlaceExitPK;

public class MudPlaceExitConverter {

	private MudPlaceExitConverter()  { }
	
	public static MudPlaceExit build(Integer placeCode, String direction, Integer targetPlaceCode) {
		
		MudPlaceExit newExit = new MudPlaceExit();
		MudPlaceExitPK newExitPK = new MudPlaceExitPK();
		
		newExitPK.setDirection(direction);
		newExitPK.setPlaceCode(placeCode);
		
		newExit.setPk(newExitPK);
		newExit.setTargetPlaceCode(targetPlaceCode);
		
		newExit.setOpened(true);
		newExit.setVisible(true);
		newExit.setLocked(false);
		newExit.setLockable(false);
		
		
		return newExit;
	}
	
	public static MudPlaceExit build(PlaceExit requestExit, Integer placeCode, String direction) {
		
		
		MudPlaceExit newExit = new MudPlaceExit();
		MudPlaceExitPK newExitPK = new MudPlaceExitPK();
		
		newExitPK.setDirection(direction);
		newExitPK.setPlaceCode(placeCode);
		
		newExit.setPk(newExitPK);
		newExit.setOpened(requestExit.isOpened());
		newExit.setVisible(requestExit.isVisible());
		newExit.setLocked(requestExit.isLocked());
		newExit.setLockable(requestExit.isLockable());
		
		newExit.setTargetPlaceCode(requestExit.getTargetPlaceCode());

		return newExit;
	}
}
