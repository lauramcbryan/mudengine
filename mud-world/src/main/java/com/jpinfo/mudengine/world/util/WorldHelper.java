package com.jpinfo.mudengine.world.util;

import java.util.HashSet;
import java.util.Set;

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
import com.jpinfo.mudengine.world.model.pk.PlaceBeingsPK;
import com.jpinfo.mudengine.world.model.pk.PlaceExitsPK;
import com.jpinfo.mudengine.world.model.pk.PlaceItemsPK;

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
	
	public static MudPlace updatePlaceBeings(MudPlace dbPlace, Place requestPlace) {
		
		if (requestPlace.getBeings()!=null) {
			
			Set<MudPlaceBeings> newBeings = new HashSet<MudPlaceBeings>();
			
			for(PlaceBeings curBeing: requestPlace.getBeings()) {
				
				MudPlaceBeings newBeing = new MudPlaceBeings();
				PlaceBeingsPK newBeingPK = new PlaceBeingsPK();
				
				newBeingPK.setBeingCode(curBeing.getBeingCode());
				newBeingPK.setPlace(requestPlace.getPlaceCode());
				
				newBeing.setPk(newBeingPK);
				newBeing.setName(curBeing.getName());
				newBeing.setQtty(curBeing.getQtty());
				
				newBeings.add(newBeing);
			}
			dbPlace.setBeings(newBeings);
		} // endif beings
		
		return dbPlace;
	}
	
	public static MudPlace updatePlaceItems(MudPlace dbPlace, Place requestPlace) {
		
		// 3. items
		if (requestPlace.getItems()!=null) {
			
			Set<MudPlaceItems> newItems = new HashSet<MudPlaceItems>();
			
			for(PlaceItems curItem: requestPlace.getItems()) {
				
				MudPlaceItems newItem = new MudPlaceItems();
				PlaceItemsPK newItemPK = new PlaceItemsPK();
				
				newItemPK.setItemCode(curItem.getItemCode());
				newItemPK.setPlaceCode(requestPlace.getPlaceCode());
				
				newItem.setPk(newItemPK);
				newItem.setName(curItem.getName());
				newItem.setQtty(curItem.getQtty());
			}
			
			dbPlace.setItems(newItems);
		}
		
		return dbPlace;
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
				newExitPK.setPlaceCode(requestPlace.getPlaceCode());
				
				newExit.setPk(newExitPK);
				newExit.setTargetPlaceCode(curExit.getTargetPlaceCode());
				newExit.setName(curExit.getName());
				newExit.setOpened(curExit.isOpened());
				newExit.setVisible(curExit.isVisible());
				newExit.setLocked(curExit.isLocked());
				newExit.setLockable(curExit.isLockable());
			}
			
			dbPlace.setExits(newExits);
			
		}
		
		return dbPlace;
		
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
