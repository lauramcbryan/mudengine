package com.jpinfo.mudengine.world.model.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.repository.PlaceRepository;

@Component
public class PlaceExitConverter {

	
	@Autowired
	private PlaceRepository repository;
	
	
	public PlaceExit convert(MudPlaceExit dbPlaceExit) {
		
		PlaceExit result = new PlaceExit();
		
		MudPlace dbTargetPlace = 
		repository.findById(dbPlaceExit.getTargetPlaceCode())
			.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_NOT_FOUND));
		
		result.setTargetPlaceCode(dbPlaceExit.getTargetPlaceCode());
		
		result.setName(dbTargetPlace.getPlaceClass().getName());
		result.setLockable(dbPlaceExit.isLockable());
		result.setLocked(dbPlaceExit.isLocked());
		result.setOpened(dbPlaceExit.isOpened());
		result.setVisible(dbPlaceExit.isVisible());
		
		return result;
	}
}
