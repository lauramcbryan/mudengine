package com.jpinfo.mudengine.world.util;

import java.util.HashSet;

import java.util.Set;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.placeclass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.PlaceAttrPK;
import com.jpinfo.mudengine.world.model.pk.PlaceExitPK;

public class WorldHelper {
	
	public static final String PLACE_HP_ATTR = "HP";
	public static final String PLACE_MAX_HP_ATTR = "MAXHP";
	
	private WorldHelper() { }

	public static Place buildPlace(MudPlace originalDbPlace) {
		
		Place result = new Place();
		
		result.setPlaceCode(originalDbPlace.getPlaceCode());
		
		result.setPlaceClassCode(originalDbPlace.getPlaceClass().getPlaceClassCode());
				
		result.setPlaceClass(WorldHelper.buildPlaceClass(originalDbPlace.getPlaceClass()));

		// Map the database list with the exits in a map
		originalDbPlace.getExits().forEach(d -> 
			result.getExits().put(
					d.getPk().getDirection(), 
					WorldHelper.buildPlaceExits(d))
		);

		originalDbPlace.getAttrs().forEach(d -> 
			result.getAttrs().put(
					d.getId().getAttrCode(), 
					d.getAttrValue())
			
		);
		
		
		return result;
	}
	
	public static PlaceClass buildPlaceClass(MudPlaceClass a) {
		
		PlaceClass response = new PlaceClass();
		
		response.setPlaceClassCode(a.getPlaceClassCode());
		response.setName(a.getName());
		response.setDescription(a.getDescription());
		response.setSizeCapacity(a.getSizeCapacity());
		response.setWeightCapacity(a.getWeightCapacity());
		response.setParentClassCode(a.getParentClassCode());
		response.setDemisePlaceClassCode(a.getDemisePlaceClassCode());
		response.setBuildEffort(a.getBuildEffort());
		response.setBuildCost(a.getBuildCost());
		
		a.getAttrs().stream().forEach(d -> 
			response.getAttrs().put(d.getId().getAttrCode(), d.getAttrValue())
		);
		
		return response;
	}
	
	public static MudPlaceAttr buildPlaceAttr(Integer placeCode, MudPlaceClassAttr classAttr) {
		
		MudPlaceAttr response = new MudPlaceAttr();
		PlaceAttrPK pk = new PlaceAttrPK();
		
		pk.setAttrCode(classAttr.getId().getAttrCode());
		pk.setPlaceCode(placeCode);
		
		response.setId(pk);
		response.setAttrValue(classAttr.getAttrValue());
		
		return response;
	}
	
	public static MudPlaceAttr buildPlaceAttr(Integer placeCode, String attrCode, Integer attrValue) {
		
		MudPlaceAttr response = new MudPlaceAttr();
		PlaceAttrPK pk = new PlaceAttrPK();
		
		pk.setAttrCode(attrCode);
		pk.setPlaceCode(placeCode);
		
		response.setId(pk);
		response.setAttrValue(attrValue);
		
		return response;
	}
	
	
	public static MudPlace updatePlaceExits(MudPlace dbPlace, Place requestPlace) {
		
		// 4. exits		
		if (requestPlace.getExits()!=null) {
			
			Set<MudPlaceExit> newExits = new HashSet<>();
			
			requestPlace.getExits().keySet().stream()
				.forEach(curDirection -> {
				
				PlaceExit curExit = requestPlace.getExits().get(curDirection);
				
				MudPlaceExit newExit = buildMudPlaceExit(dbPlace.getPlaceCode(), curDirection, curExit.getTargetPlaceCode());

				newExit.setName(curExit.getName());
				newExit.setOpened(curExit.isOpened());
				newExit.setVisible(curExit.isVisible());
				newExit.setLocked(curExit.isLocked());
				newExit.setLockable(curExit.isLockable());
				
				newExits.add(newExit);
			});
			
			// As hibernate manages the child list returned by him, we must not to create
			// a new list, but to clear the existing one to force DELETE/UPDATE of changed entries
			dbPlace.getExits().clear();
			dbPlace.getExits().addAll(newExits);
			
		}
		
		return dbPlace;
		
	}
	
	public static MudPlaceExit buildMudPlaceExit(Integer placeCode, String direction, Integer targetPlaceCode) {
		
		MudPlaceExit newExit = new MudPlaceExit();
		PlaceExitPK newExitPK = new PlaceExitPK();
		
		newExitPK.setDirection(direction);
		newExitPK.setPlaceCode(placeCode);
		
		newExit.setPk(newExitPK);
		newExit.setTargetPlaceCode(targetPlaceCode);
		
		return newExit;
	}
	
	private static PlaceExit buildPlaceExits(MudPlaceExit a) {
		
		PlaceExit result = new PlaceExit();
		
		result.setTargetPlaceCode(a.getTargetPlaceCode());
		
		result.setName(a.getName());
		result.setLockable(a.isLockable());
		result.setLocked(a.isLocked());
		result.setOpened(a.isOpened());
		result.setVisible(a.isVisible());
		
		return result;
	}
	
	public static String getOpposedDirection(String direction) {
		
		int size = PlaceExit.DIRECTIONS.size(); 
		int originalPos = PlaceExit.DIRECTIONS.indexOf(direction);
		
		int newpos = size - originalPos - 1;
		
		return PlaceExit.DIRECTIONS.get(newpos);
	}

}
