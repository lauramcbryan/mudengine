package com.jpinfo.mudengine.world.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.PlaceService;
import com.jpinfo.mudengine.world.client.BeingServiceClient;
import com.jpinfo.mudengine.world.client.ItemServiceClient;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.repository.PlaceExitRepository;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@RestController
public class PlaceController implements PlaceService {
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private BeingServiceClient beingService;
	
	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private PlaceExitRepository placeExitRepository;
	
	@Autowired
	private PlaceClassRepository placeClassRepository;

	@Override
	public Place getPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Integer placeId) {
		
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
	public Place updatePlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Integer placeId, @RequestBody Place requestPlace) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository.findOne(placeId);
		
		if (dbPlace!=null) {
		
			// Check if the current HP of the place is lower than zero
			Integer currentHP = requestPlace.getAttrs().get(WorldHelper.PLACE_HP_ATTR);
			Integer maxHP = requestPlace.getAttrs().get(WorldHelper.PLACE_MAX_HP_ATTR);
			
			if (currentHP!=null) {
				
				if (currentHP < 0) {
					
					dbPlace.setPlaceCode(null);
					response = WorldHelper.buildPlace(dbPlace);
		
					// destroy the place
					destroyPlace(authToken, placeId);
					
					
					
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
			
			// Looking for attributes to remove
			for(MudPlaceAttr curAttr: dbPlace.getAttrs()) {
				
				// if doesn't exist in request entity, remove it
				if (requestPlace.getAttrs().get(curAttr.getId().getAttrCode())==null) {
					dbPlace.getAttrs().remove(curAttr);
				}
			}
			
			// Looking for attributes to add
			for(String curAttr: requestPlace.getAttrs().keySet()) {
				
				boolean found = false;
				for(MudPlaceAttr curDbAttr: dbPlace.getAttrs()) {
					
					if (curDbAttr.getId().getAttrCode().equals(curAttr)) {
						curDbAttr.setAttrValue(requestPlace.getAttrs().get(curAttr));
						found = true;
					}
				}
				
				if (!found) {
					
					dbPlace.getAttrs().add(
							WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curAttr, requestPlace.getAttrs().get(curAttr))
							);
				}
				
			}
			
			// if placeClass is changed, update the attributes of changed place
			if (!dbPlace.getPlaceClass().getPlaceClassCode().equals(requestPlace.getPlaceClassCode())) {
				
				// change placeClass
				MudPlaceClass placeClass = placeClassRepository.findOne(requestPlace.getPlaceClassCode());
				
				dbPlace = WorldHelper.changePlaceAttrs(dbPlace, dbPlace.getPlaceClass(), placeClass);
				dbPlace.setPlaceClass(placeClass);
				
				// Find all exits pointing to this place
				Iterable<MudPlaceExit> exitList = placeExitRepository.findByTargetPlaceCode(dbPlace.getPlaceCode());
				
				// Update them all
				for(MudPlaceExit curExit: exitList) {
					
					curExit.setName(placeClass.getName());
					placeExitRepository.save(curExit);
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
	public void destroyPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Integer placeId) {
		
		MudPlace dbPlace = placeRepository.findOne(placeId);
		
		if (dbPlace!=null) {
			
			MudPlace updatedPlace = null;
			
			if (dbPlace.getPlaceClass().getDemisePlaceClassCode()!=null) {
				MudPlaceClass placeClass = placeClassRepository.findOne(dbPlace.getPlaceClass().getDemisePlaceClassCode());
				
				dbPlace = WorldHelper.changePlaceAttrs(dbPlace, dbPlace.getPlaceClass(), placeClass);
				dbPlace.setPlaceClass(placeClass);
				
				// Find all exits pointing to this place
				Iterable<MudPlaceExit> exitList = placeExitRepository.findByTargetPlaceCode(dbPlace.getPlaceCode());
				
				// Update them all
				for(MudPlaceExit curExit: exitList) {
					
					curExit.setName(placeClass.getName());
					placeExitRepository.save(curExit);
				}
				
				
				updatedPlace = placeRepository.save(dbPlace);
			} else {
				
				// Destroy the place
				placeRepository.delete(dbPlace);
				
				String internalToken = TokenService.buildInternalToken();
				
				try {
				
					// Remove all beings from the place
					// THAT MUST GOES FIRST!!!
					// This call will drop all items belonging to beings into the place
					// @TODO: solve the worldName
					beingService.destroyAllFromPlace(internalToken, "aforgotten", placeId);
					
					// Remove all items from the place
					// (That will include items dropped from beings above)
					// @TODO: solve the worldName
					itemService.destroyAllFromPlace(internalToken, "aforgotten", placeId);
					
				} catch(Exception e) {
					
					// Any exception on these calls will be disregarded
					e.printStackTrace(System.err);
				}
				
				updatedPlace = dbPlace;
				updatedPlace.setPlaceCode(null);
			}
			
		} else {
			// Returns a 404 error
			throw new EntityNotFoundException("Place entity not found");
		}
	}


	@Override
	public ResponseEntity<Place> createPlace(String placeClassCode, String direction, Integer targetPlaceCode) {
		
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
					WorldHelper.changePlaceAttrs(dbPlace, null, dbPlaceClass);
		
					// Creating the new exit
					MudPlaceExit newExit = WorldHelper.buildMudPlaceExit(dbPlace.getPlaceCode(), 
							direction, targetPlaceCode);
					newExit.setName(targetDbPlace.getPlaceClass().getName());
					newExit.setOpened(true);
					newExit.setVisible(true);
					
					dbPlace.getExits().add(newExit);
					
					// Updating the place in database
					dbPlace = placeRepository.save(dbPlace);
					
					// Updating the targetPlace exit to have a corresponding exit to new place created
					MudPlaceExit correspondingExit = WorldHelper.buildMudPlaceExit(targetDbPlace.getPlaceCode(), 
							WorldHelper.getOpposedDirection(direction), 
							dbPlace.getPlaceCode());
					correspondingExit.setName(dbPlaceClass.getName());
					correspondingExit.setOpened(true);
					correspondingExit.setVisible(true);
					
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
		
		return new ResponseEntity<Place>(response, HttpStatus.CREATED);
	}	
	
}
