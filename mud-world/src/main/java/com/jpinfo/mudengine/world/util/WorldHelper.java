package com.jpinfo.mudengine.world.util;

import java.util.HashSet;
import java.util.Set;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.placeClass.PlaceClass;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.pk.PlaceAttrPK;
import com.jpinfo.mudengine.world.model.pk.PlaceExitPK;

public class WorldHelper {
	
	public static final String PLACE_HP_ATTR = "HP";
	public static final String PLACE_MAX_HP_ATTR = "MAX_HP";

	public static Place buildPlace(MudPlace a) {
		
		Place result = new Place();
		
		result.setPlaceCode(a.getPlaceCode());
		
		result.setPlaceClassCode(a.getPlaceClass().getPlaceClassCode());
		
		for(MudPlaceExit curExit: a.getExits()) {
			result.getExits().put(curExit.getPk().getDirection(), WorldHelper.buildPlaceExits(curExit));
		}
		
		for(MudPlaceAttr curAttr: a.getAttrs()) {
			
			result.getAttrs().put(curAttr.getId().getAttrCode(), curAttr.getAttrValue());
			
		}
		
		
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
		
		for(MudPlaceClassAttr curAttr: a.getAttrs()) {
			
			response.getAttrs().put(curAttr.getId().getAttrCode(), curAttr.getAttrValue());
			
		}
		
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
			
			Set<MudPlaceExit> newExits = new HashSet<MudPlaceExit>();
			
			for(String curDirection: requestPlace.getExits().keySet()) {
				
				PlaceExit curExit = requestPlace.getExits().get(curDirection);
				
				MudPlaceExit newExit = buildMudPlaceExit(dbPlace.getPlaceCode(), curDirection, curExit.getTargetPlaceCode());

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
	
	public static MudPlace changePlaceAttrs(MudPlace dbPlace, MudPlaceClass previousPlaceClass, MudPlaceClass placeClass) {
		
		if (previousPlaceClass!=null) {
		
			for(MudPlaceClassAttr curClassAttr: previousPlaceClass.getAttrs()) {
				
				MudPlaceAttr oldAttr = WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curClassAttr);
				
				dbPlace.getAttrs().remove(oldAttr);
			}
		}
		
		for(MudPlaceClassAttr curClassAttr: placeClass.getAttrs()) {
			
			MudPlaceAttr newAttr = WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curClassAttr);
			
			dbPlace.getAttrs().add(newAttr);
		}

		return dbPlace;
	}
	
}
