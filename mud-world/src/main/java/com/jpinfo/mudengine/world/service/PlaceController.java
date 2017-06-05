package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExits;
import com.jpinfo.mudengine.common.service.PlaceService;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@RestController
public class PlaceController implements PlaceService {
	
	@Autowired
	private PlaceRepository placeRepository;
	
	@Autowired
	private PlaceClassRepository placeClassRepository;

	@Override
	public Place getPlace(@PathVariable Integer id) {
		
		MudPlace found = placeRepository.findOne(id);
		
		Place result = WorldHelper.buildPlace(found);
		
		return result;
	}

	
	@Override
	public Place updatePlace(@PathVariable Integer id, @RequestBody Place requestPlace) {
		
		MudPlace dbPlace = placeRepository.findOne(id);

		// What can be updated:
		
		// 1. placeClass
		if (requestPlace.getPlaceClassCode()!=null) {
			
			String requestPlaceClassCode = requestPlace.getPlaceClassCode();
			
			if (!dbPlace.getPlaceClass().getPlaceClassCode().equals(requestPlaceClassCode)) {

				MudPlaceClass foundPlaceClass = placeClassRepository.findOne(requestPlaceClassCode);
				
				dbPlace.setPlaceClass(foundPlaceClass);
			}
		}
		
		// 4. exits
		dbPlace = WorldHelper.updatePlaceExits(dbPlace, requestPlace);

		
		MudPlace updatedPlace = placeRepository.save(dbPlace);
		
		return WorldHelper.buildPlace(updatedPlace);
	}


	@Override
	public Place destroyPlace(Integer placeId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Place createExit(Integer placeId, String direction, PlaceExits newExit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Place updateExit(Integer placeId, String direction, PlaceExits newExit) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Place destroyExit(Integer placeId, String direction) {
		// TODO Auto-generated method stub
		return null;
	}
}
