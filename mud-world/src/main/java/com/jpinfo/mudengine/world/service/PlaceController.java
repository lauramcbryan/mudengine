package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.service.PlaceService;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceClassAttr;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
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
	public Place getPlace(@PathVariable Integer placeId) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository.findOne(placeId);
		
		if (dbPlace!=null) {
			response = WorldHelper.buildPlace(dbPlace);
		} else {
			throw new EntityNotFoundException("Place entity not found");
		}
		
		return response;
	}

	
	@Override
	public Place updatePlace(@PathVariable Integer placeId, @RequestBody Place requestPlace) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository.findOne(placeId);
		
		if (dbPlace!=null) {
		
			// if placeClass is changed, update the attributes of changed place
			if (!dbPlace.getPlaceClass().getPlaceClassCode().equals(requestPlace.getPlaceClassCode())) {
				
				// change placeClass
				MudPlaceClass placeClass = placeClassRepository.findOne(requestPlace.getPlaceClassCode());
				
				dbPlace.setPlaceClass(placeClass);
				dbPlace = resetPlaceAttrs(dbPlace, placeClass);
			}
	
			// Check if the current HP of the place is lower than zero
			Integer currentHP = requestPlace.getAttrs().get(WorldHelper.PLACE_HP_ATTR);
			Integer maxHP = requestPlace.getAttrs().get(WorldHelper.PLACE_MAX_HP_ATTR);
			
			if (currentHP!=null) {
				
				if (currentHP < 0) {
		
					// destroy the place
					// Update the placeClass to demised placeClass
					MudPlaceClass placeClass = placeClassRepository.findOne(dbPlace.getPlaceClass().getDemisePlaceClassCode());
					
					dbPlace.setPlaceClass(placeClass);
					dbPlace = resetPlaceAttrs(dbPlace, placeClass);
					
				} else {
					// If HP is greater than MAX_HP, adjust it
					if ((maxHP!=null) && (currentHP > maxHP)) {
						
						for(MudPlaceAttr curAttr: dbPlace.getAttrs()) {
							
							if (curAttr.getId().getAttrCode().equals(WorldHelper.PLACE_HP_ATTR)) {
								curAttr.setAttrValue(maxHP);
							}
						}
					}
				}
			}
			
			// 4. exits
			dbPlace = WorldHelper.updatePlaceExits(dbPlace, requestPlace);
	
			
			MudPlace updatedPlace = placeRepository.save(dbPlace);
			
			response = WorldHelper.buildPlace(updatedPlace);
			
		} else {
			// Returns a 404 error
			throw new EntityNotFoundException("Place entity not found");
		}
		
		return response;
	}


	@Override
	public Place destroyPlace(@PathVariable Integer placeId) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository.findOne(placeId);
		
		if (dbPlace!=null) {
			
			MudPlace updatedPlace = null;
			
			if (dbPlace.getPlaceClass().getDemisePlaceClassCode()!=null) {
				MudPlaceClass placeClass = placeClassRepository.findOne(dbPlace.getPlaceClass().getDemisePlaceClassCode());
				
				dbPlace.setPlaceClass(placeClass);
				dbPlace = resetPlaceAttrs(dbPlace, placeClass);
				
				updatedPlace = placeRepository.save(dbPlace);
			} else {
				
				placeRepository.delete(dbPlace);
				
				updatedPlace = dbPlace;
				updatedPlace.setPlaceCode(null);
			}
			
			
			response = WorldHelper.buildPlace(updatedPlace);
			
		} else {
			// Returns a 404 error
			throw new EntityNotFoundException("Place entity not found");
		}
		
		return response;
	}


	@Override
	public Place createPlace(String placeClassCode, String direction, Integer targetPlaceCode) {
		
		Place response = null;
		
		// Retrieving the placeClass
		MudPlaceClass dbPlaceClass = placeClassRepository.findOne(placeClassCode);
		
		// Retrieving the targetPlace
		MudPlace targetDbPlace = placeRepository.findOne(targetPlaceCode);
		
		if(targetDbPlace!=null) {
			
			if (dbPlaceClass!=null) {
				
				// Check the corresponding exit of target place to be update in this flow
				String correspondingDirection = WorldHelper.getOpposedDirection(direction);
				
				boolean found = false;
				for(MudPlaceExit curExit: targetDbPlace.getExits()) {
					
					if (curExit.getPk().getDirection().equals(correspondingDirection)) {
						found = true;
					}
				}
				
				if (!found) {

					MudPlace newPlace = new MudPlace();
					newPlace.setPlaceClass(dbPlaceClass);
		
					// Saving in database with minimum information in order to have the placeId
					MudPlace dbPlace = placeRepository.save(newPlace);
					
					// Updating attributes
					resetPlaceAttrs(dbPlace, dbPlaceClass);
		
					// Creating the new exit
					MudPlaceExit newExit = WorldHelper.buildMudPlaceExit(dbPlace.getPlaceCode(), 
							direction, targetPlaceCode);
					newExit.setName(targetDbPlace.getPlaceClass().getName());
					
					dbPlace.getExits().add(newExit);
					
					// Updating the place in database
					dbPlace = placeRepository.save(dbPlace);
					
					// Updating the targetPlace exit to have a corresponding exit to new place created
					MudPlaceExit correspondingExit = WorldHelper.buildMudPlaceExit(targetDbPlace.getPlaceCode(), 
							WorldHelper.getOpposedDirection(direction), 
							dbPlace.getPlaceCode());
					correspondingExit.setName(dbPlaceClass.getName());
					
					targetDbPlace.getExits().add(correspondingExit);
					
					placeRepository.save(targetDbPlace);
					
					response = WorldHelper.buildPlace(dbPlace);
				}
				else {
					throw new RuntimeException("Target place already has a corresponding exit");
				}
				
			} else {
				throw new EntityNotFoundException("Place Class entity not found");
			}
			
		} else {
			// returns a http 404 error
			throw new EntityNotFoundException("Target Place entity not found");
		}
		
		return response;
	}
	
	private MudPlace resetPlaceAttrs(MudPlace dbPlace, MudPlaceClass placeClass) {
		
		dbPlace.getAttrs().clear();
		for(MudPlaceClassAttr curClassAttr: placeClass.getAttrs()) {
			
			MudPlaceAttr newAttr = WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curClassAttr);
			
			dbPlace.getAttrs().add(newAttr);
		}

		return dbPlace;
	}
	
	
}
