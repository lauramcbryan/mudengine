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
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.service.PlaceService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
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

import io.swagger.annotations.*;

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
	@ApiOperation(value="Returns information about a place")
	public Place getPlace(
			@ApiParam(value="Authentication token", allowEmptyValue=false, required=true) @RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@PathVariable Integer placeId) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));
		
		response = WorldHelper.buildPlace(dbPlace);
		
		return response;
	}

	
	@Override
	public Place updatePlace(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Integer placeId, @RequestBody Place requestPlace) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));
		
		
		// 1.. Check place attributes
		// ============================================
		internalSyncAttr(dbPlace, requestPlace);
		
		
		// 2.. Check place HP
		// ============================================
		boolean placeToBeDestroyed = internalSyncPlaceHealth(dbPlace, requestPlace);
		
		if (placeToBeDestroyed) {
			
			// destroy the place
			destroyPlace(authToken, dbPlace.getPlaceCode());
			
		} else {

			// 3.. Check place class
			// ============================================
			
			// if placeClass is changed, resync the attributes of changed place
			if (!dbPlace.getPlaceClass().getPlaceClassCode().equals(requestPlace.getPlaceClassCode())) {
	
				// change placeClass			
				internalUpdateClass(dbPlace, requestPlace.getPlaceClassCode());
				
			}
			
			// 4.. Check place exits
			// ============================================
			
			dbPlace = WorldHelper.updatePlaceExits(dbPlace, requestPlace);
	
			// updating the place in database
			MudPlace updatedPlace = placeRepository.save(dbPlace);
			
			// Mounting the response
			response = WorldHelper.buildPlace(updatedPlace);
		}
		
		return response;
	}
	
	private boolean internalSyncPlaceHealth(final MudPlace dbPlace, final Place requestPlace) {

		boolean placeDestroyed = false;
		
		// Check current place health
		// First, we obtain the maxHP for this place
		// if this value is different from zero, it means that this is a place that can be destroyed
		Integer maxHP = 
				dbPlace.getAttrs().stream()
					.filter(d-> d.getId().getAttrCode().equals(WorldHelper.PLACE_MAX_HP_ATTR))
					.mapToInt(MudPlaceAttr::getAttrValue)
					.findFirst()
					.orElse(0);
		
		// Retrieve the current HP of the place.  That value came from the request
		Integer currentHP = requestPlace.getAttrs().getOrDefault(WorldHelper.PLACE_HP_ATTR, 0);
		
		// If the currentPlace has an HP and it is exhausted		
		placeDestroyed = (maxHP!=0) && (currentHP <=0);
		
		if ((maxHP!=0) && (currentHP > maxHP)) {
			
			// Adjusts the currentHP to the maximum			
			dbPlace.getAttrs().stream()
				.filter(d -> d.getId().getAttrCode().equals(WorldHelper.PLACE_HP_ATTR))
				.findFirst()
				.ifPresent(e -> e.setAttrValue(maxHP));
		}
		
		return placeDestroyed;
	}
	
	
	private MudPlace internalSyncAttr(MudPlace dbPlace, MudPlaceClass previousPlaceClass, MudPlaceClass placeClass) {
		
		if (previousPlaceClass!=null) {
			
			previousPlaceClass.getAttrs().stream()
				.forEach(curClassAttr -> {
					MudPlaceAttr oldAttr = WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curClassAttr);
					dbPlace.getAttrs().remove(oldAttr);
			});
			
		}
		
		placeClass.getAttrs().stream()
			.forEach(curClassAttr -> {
				MudPlaceAttr newAttr = WorldHelper.buildPlaceAttr(dbPlace.getPlaceCode(), curClassAttr);
				dbPlace.getAttrs().add(newAttr);
		});

		return dbPlace;
	}
	
	private MudPlace internalSyncAttr(final MudPlace dbPlace, final Place requestPlace) {
		
		// Looking for attributes to remove
		Set<MudPlaceAttr> filteredSet =
			dbPlace.getAttrs().stream()
				// Filtering all database attributes...
				.filter(db -> requestPlace.getAttrs().keySet().stream()
						// ... that isn't in the request
						.noneMatch(req -> req.equals(db.getId().getAttrCode())))
				.collect(Collectors.toSet());

		
		dbPlace.getAttrs().removeAll(filteredSet);
			

		// Looking for attributes to add/update
		for(String curAttr: requestPlace.getAttrs().keySet()) {
			
			Integer curValue = requestPlace.getAttrs().get(curAttr);
			
			// Looking for existing attribute in db record list
			Optional<MudPlaceAttr> foundAttr = 
				dbPlace.getAttrs().stream()
					.filter(a -> a.getId().getAttrCode().equals(curAttr))
					.findFirst();

			// If the value exists in db record
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

		return dbPlace;
	}
	
	private MudPlace internalUpdateClass(MudPlace original, String newPlaceClassCode) {
		
		MudPlaceClass placeClass = placeClassRepository
				.findById(newPlaceClassCode)
				.orElseThrow(() -> new EntityNotFoundException("Place class entity not found"));
		

		internalSyncAttr(original, original.getPlaceClass(), placeClass);
		original.setPlaceClass(placeClass);

		// Find all exits pointing to this place
		Iterable<MudPlaceExit> exitList = placeExitRepository.findByTargetPlaceCode(original.getPlaceCode());
		
		// Update them all
		exitList.forEach(curExit -> {
			curExit.setName(placeClass.getName());
			placeExitRepository.save(curExit);
			
		});
		
		return original;
	}


	@Override
	public void destroyPlace(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Integer placeId) {
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place entity not found"));

		// If exists a demise place class for this location
		if (dbPlace.getPlaceClass().getDemisePlaceClassCode()!=null) {

			// Change the placeClass to the demised one
			internalUpdateClass(dbPlace, dbPlace.getPlaceClass().getDemisePlaceClassCode());
			
			placeRepository.save(dbPlace);
			
		} else {
			
			// Destroy the place
			placeRepository.delete(dbPlace);
			
			try {
			
				// Remove all beings from the place
				// THAT MUST GOES FIRST!!!
				// This call will drop all items belonging to beings into the place
				// TODO: solve the worldName
				beingService.destroyAllFromPlace(authToken, "aforgotten", placeId);
				
				// Remove all items from the place
				// (That will include items dropped from beings above)
				// TODO: solve the worldName
				itemService.destroyAllFromPlace(authToken, "aforgotten", placeId);
				
			} catch(Exception e) {
				
				// Any exception on these calls will be disregarded
				// TODO: Log this exception
			}
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
			
			throw new IllegalParameterException("Target place already has a corresponding exit");
		}
		
		MudPlace newPlace = new MudPlace();
		newPlace.setPlaceClass(dbPlaceClass);

		// Saving in database with minimum information in order to have the placeId
		MudPlace dbPlace = placeRepository.save(newPlace);
		
		// Updating attributes
		internalSyncAttr(dbPlace, null, dbPlaceClass);

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
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}	
	
}
