package com.jpinfo.mudengine.world.util;

import java.util.HashSet;
import java.util.Set;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExits;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceExits;
import com.jpinfo.mudengine.world.model.pk.PlaceExitsPK;

public class WorldHelper {

	public static Place buildPlace(MudPlace a) {
		
		Place result = new Place();
		
		result.setPlaceCode(a.getPlaceCode());
		
		result.setPlaceClassCode(a.getPlaceClass().getPlaceClassCode());
		
		for(MudPlaceExits curExit: a.getExits()) {
			result.getExits().put(curExit.getPk().getDirection(), WorldHelper.buildPlaceExits(curExit));
		}
		
		return result;
	}
	
	public static PlaceClass buildPlaceClass(MudPlaceClass a) {
		
		return new PlaceClass();
	}
	
	
	public static MudPlace updatePlaceExits(MudPlace dbPlace, Place requestPlace) {
		
		// 4. exits		
		if (requestPlace.getExits()!=null) {
			
			Set<MudPlaceExits> newExits = new HashSet<MudPlaceExits>();
			
			for(String curDirection: requestPlace.getExits().keySet()) {
				
				PlaceExits curExit = requestPlace.getExits().get(curDirection);
				
				MudPlaceExits newExit = new MudPlaceExits();
				PlaceExitsPK newExitPK = new PlaceExitsPK();
				
				newExitPK.setDirection(curDirection);
				newExitPK.setPlaceCode(dbPlace.getPlaceCode());
				
				newExit.setPk(newExitPK);
				newExit.setTargetPlaceCode(curExit.getTargetPlaceCode());
				newExit.setName(curExit.getName());
				newExit.setOpened(curExit.isOpened());
				newExit.setVisible(curExit.isVisible());
				newExit.setLocked(curExit.isLocked());
				newExit.setLockable(curExit.isLockable());
				
				newExits.add(newExit);
			}
			
			// As hibernate manages the child list returned by him, we must not to create
			// a new list, but to clear the existing one to force DELETE/UPDATE of changed entries
			dbPlace.getExits().clear();
			dbPlace.getExits().addAll(newExits);
			
		}
		
		return dbPlace;
		
	}
	
	private static PlaceExits buildPlaceExits(MudPlaceExits a) {
		
		PlaceExits result = new PlaceExits();
		
		result.setName(a.getName());
		
		result.setTargetPlaceCode(a.getTargetPlaceCode());
		
		result.setLockable(a.isLockable());
		result.setLocked(a.isLocked());
		result.setOpened(a.isOpened());
		result.setVisible(a.isVisible());
		
		return result;
	}
}
