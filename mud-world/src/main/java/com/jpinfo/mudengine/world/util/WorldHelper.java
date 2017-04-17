package com.jpinfo.mudengine.world.util;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceBeings;
import com.jpinfo.mudengine.common.place.PlaceExits;
import com.jpinfo.mudengine.common.place.PlaceItems;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceBeings;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceExits;
import com.jpinfo.mudengine.world.model.MudPlaceItems;

public class WorldHelper {

	public static Place buildPlace(MudPlace a) {
		
		Place result = new Place();
		
		result.setPlaceCode(a.getPlaceCode());
		
		result.setPlaceClass(WorldHelper.buildPlaceClass(a.getPlaceClass()));
		
		for(MudPlaceBeings curBeing: a.getBeings()) {
			result.getBeings().add(WorldHelper.buildPlaceBeing(curBeing));
		}
		
		for(MudPlaceExits curExit: a.getExits()) {
			result.getExits().put(curExit.getPk().getDirection(), WorldHelper.buildPlaceExits(curExit));
		}
		
		for(MudPlaceItems curItem: a.getItems()) {
			result.getItems().add(WorldHelper.buildPlaceItems(curItem));
		}
		
		return new Place();
	}
	
	public static PlaceClass buildPlaceClass(MudPlaceClass a) {
		
		return new PlaceClass();
	}
	
	private static PlaceBeings buildPlaceBeing(MudPlaceBeings a) {
		
		PlaceBeings result = new PlaceBeings();
		
		result.setBeingCode(a.getPk().getBeingCode());
		
		// @TODO: MudPlaceBeings name
		//result.setName(a.);
		result.setQtty(a.getQtty());
		
		
		return result;
	}
	
	private static PlaceExits buildPlaceExits(MudPlaceExits a) {
		
		PlaceExits result = new PlaceExits();
		
		result.setDirection(a.getPk().getDirection());
		result.setName(a.getName());
		
		result.setTargetPlaceCode(a.getTargetPlaceCode());
		
		result.setLockable(a.isLockable());
		result.setLocked(a.isLocked());
		result.setOpened(a.isOpened());
		result.setVisible(a.isVisible());
		
		return result;
	}
	
	private static PlaceItems buildPlaceItems(MudPlaceItems a) {
		
		PlaceItems result = new PlaceItems();
		
		result.setItemCode(a.getPk().getItemCode());
		
		// @TODO: MudPlaceItems name
		//result.setName(name);
		result.setQtty(a.getQtty());
		
		return result;
	}
}
