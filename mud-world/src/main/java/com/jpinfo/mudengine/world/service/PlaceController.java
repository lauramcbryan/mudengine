package com.jpinfo.mudengine.world.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));
		
		response = WorldHelper.buildPlace(dbPlace);
		
		return response;
	}

	
	@Override
	public Place updatePlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Integer placeId, @RequestBody Place requestPlace) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));
		
		// Check if the current HP of the place is lower than zero
		Integer currentHP = requestPlace.getAttrs().get(WorldHelper.PLACE_HP_ATTR);
		Integer nextHP = currentHP;
		
		if (currentHP!=null) {
			
			if (currentHP < 0) {
				
				dbPlace.setPlaceCode(null);
				response = WorldHelper.buildPlace(dbPlace);
	
				// destroy the place
				destroyPlace(authToken, placeId);
				
				
			} else {

				// Getting the current MAX HP from the place
				Optional<MudPlaceAttr> maxHPAttr = 
					dbPlace.getAttrs().stream()
						.filter(d-> d.getId().getAttrCode().equals(WorldHelper.PLACE_MAX_HP_ATTR))
						.findFirst();
				
				if (maxHPAttr.isPresent()) {
					
					// If HP is greater than MAX_HP, adjust it						
					if (maxHPAttr.get().getAttrValue() < nextHP) {
						
						nextHP = maxHPAttr.get().getAttrValue();
						
					}
				}
				

				// Update the hp attr
				Optional<MudPlaceAttr> hpAttr = 
					dbPlace.getAttrs().stream()
						.filter(d-> d.getId().getAttrCode().equals(WorldHelper.PLACE_HP_ATTR))
						.findFirst();
				
				if (hpAttr.isPresent()) {
					hpAttr.get().setAttrValue(nextHP);
				} else {
					dbPlace.getAttrs().add(
							WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), WorldHelper.PLACE_HP_ATTR, nextHP)
							);
				}

			}
		}
			
		// Looking for attributes to remove
		Set<MudPlaceAttr> filteredSet =
			dbPlace.getAttrs().stream()
				// Filtering all database attributes...
				.filter(db -> requestPlace.getAttrs().keySet().stream()
						// ... that isn't in the request
						.noneMatch(req -> req.equals(db.getId().getAttrCode())))
				.collect(Collectors.toSet());

		
		dbPlace.getAttrs().removeAll(filteredSet);
			
		// Looking for attributes to add
		for(String curAttr: requestPlace.getAttrs().keySet()) {
			
			Integer curValue = requestPlace.getAttrs().get(curAttr);

			// Looking for existing attribute in db record list
			Optional<MudPlaceAttr> foundAttr = 
				dbPlace.getAttrs().stream()
					.filter(a -> a.getId().getAttrCode().equals(curAttr))
					.findFirst();

			if (foundAttr.isPresent()) {
				
				// Updates the value of existing attribute
				foundAttr.get().setAttrValue(curValue);
			} else {
				
				// Creates a new attribute
				dbPlace.getAttrs().add(
						WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curAttr, curValue)
						);
			}
		}
			
		// if placeClass is changed, update the attributes of changed place
		if (!dbPlace.getPlaceClass().getPlaceClassCode().equals(requestPlace.getPlaceClassCode())) {
			
			// change placeClass
			MudPlaceClass placeClass = placeClassRepository
					.findById(requestPlace.getPlaceClassCode())
					.orElseThrow(() -> new EntityNotFoundException("Place class entity not found"));
			
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
		
		return response;
	}


	@Override
	public void destroyPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Integer placeId) {
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));

		MudPlace updatedPlace = null;
			
		// If exists a demise place class for this location
		if (dbPlace.getPlaceClass().getDemisePlaceClassCode()!=null) {
			
			Optional<MudPlaceClass> placeClass = placeClassRepository
					.findById(dbPlace.getPlaceClass().getDemisePlaceClassCode());
			
			if (placeClass.isPresent()) {
				
				MudPlaceClass d = placeClass.get();

				dbPlace = WorldHelper.changePlaceAttrs(dbPlace, dbPlace.getPlaceClass(), d);
				dbPlace.setPlaceClass(d);

				// Find all exits pointing to this place
				Iterable<MudPlaceExit> exitList = placeExitRepository.findByTargetPlaceCode(dbPlace.getPlaceCode());
				
				// Update them all
				exitList.forEach(curExit -> {
					curExit.setName(d.getName());
					placeExitRepository.save(curExit);
					
				});

				// Update the main place
				updatedPlace = placeRepository.save(dbPlace);
			}
			
		} else {
			
			// Destroy the place
			placeRepository.delete(dbPlace);
			
			String internalToken = TokenService.buildInternalToken();
			
			try {
			
				// Remove all beings from the place
				// THAT MUST GOES FIRST!!!
				// This call will drop all items belonging to beings into the place
				// TODO: solve the worldName
				beingService.destroyAllFromPlace(internalToken, "aforgotten", placeId);
				
				// Remove all items from the place
				// (That will include items dropped from beings above)
				// TODO: solve the worldName
				itemService.destroyAllFromPlace(internalToken, "aforgotten", placeId);
				
			} catch(Exception e) {
				
				// Any exception on these calls will be disregarded
				e.printStackTrace(System.err);
			}
			
			updatedPlace = dbPlace;
			updatedPlace.setPlaceCode(null);
		}
	}


	@Override
	public ResponseEntity<Place> createPlace(String placeClassCode, String direction, Integer targetPlaceCode) {
		
		Place response = null;
		
		// Retrieving the placeClass
		MudPlaceClass dbPlaceClass = placeClassRepository
				.findById(placeClassCode)
				.orElseThrow(() -> new EntityNotFoundException("Place Class entity not found"));
		
		// Retrieving the targetPlace
		MudPlace targetDbPlace = placeRepository
				.findById(targetPlaceCode)
				.orElseThrow(() -> new EntityNotFoundException("Target Place entity not found"));
		
		// Check the corresponding exit of target place to be update in this flow
		String correspondingDirection = WorldHelper.getOpposedDirection(direction);

		// Check if the target place already has an exit to this direction
		if (targetDbPlace.getExits().stream()
			.anyMatch(d -> d.getPk().getDirection().equals(correspondingDirection))) {
			
			throw new RuntimeException("Target place already has a corresponding exit");
		}
		
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
		
		return new ResponseEntity<Place>(response, HttpStatus.CREATED);
	}	
	
}
