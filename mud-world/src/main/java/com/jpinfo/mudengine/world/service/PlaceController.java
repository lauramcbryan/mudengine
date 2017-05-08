package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.interfaces.PlaceService;
import com.jpinfo.mudengine.common.place.Place;
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
	public void updatePlace(@PathVariable Integer id, @RequestBody Place requestPlace) {
		
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
		
		// 2. Beings
		dbPlace = WorldHelper.updatePlaceBeings(dbPlace, requestPlace);
		
		// 3. items
		dbPlace = WorldHelper.updatePlaceItems(dbPlace, requestPlace);
		
		// 4. exits
		dbPlace = WorldHelper.updatePlaceExits(dbPlace, requestPlace);

		
		placeRepository.save(dbPlace);
	}
}
