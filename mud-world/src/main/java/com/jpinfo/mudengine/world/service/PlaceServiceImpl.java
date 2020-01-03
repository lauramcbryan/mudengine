package com.jpinfo.mudengine.world.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.world.model.MudPlace;
import com.jpinfo.mudengine.world.model.MudPlaceAttr;
import com.jpinfo.mudengine.world.model.MudPlaceClass;
import com.jpinfo.mudengine.world.model.MudPlaceExit;
import com.jpinfo.mudengine.world.model.converter.MudPlaceAttrConverter;
import com.jpinfo.mudengine.world.model.converter.MudPlaceExitConverter;
import com.jpinfo.mudengine.world.model.converter.PlaceConverter;
import com.jpinfo.mudengine.world.repository.PlaceClassRepository;
import com.jpinfo.mudengine.world.repository.PlaceRepository;
import com.jpinfo.mudengine.world.util.WorldHelper;

@Service
public class PlaceServiceImpl {

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private PlaceClassRepository placeClassRepository;

	public Place getPlace(Integer placeId) {
		
		return placeRepository
				.findById(placeId)
				.map(PlaceConverter::convert)
				.map(this::updateExitNames)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_NOT_FOUND));
	}
	
	private Place updateExitNames(Place originalPlace) {
		
		originalPlace.getExits().keySet().stream()
			.forEach(curDirection -> {
				
				PlaceExit curExit = originalPlace.getExits().get(curDirection);
				
				curExit.setName(
						placeRepository.findById(curExit.getTargetPlaceCode())
						.map(d -> d.getPlaceClass().getName())
						.orElse(null)
						);
				
			});
		
		return originalPlace;
	}

	
	public Place updatePlace(Integer placeId, Place requestPlace) {
		
		Place response = null;
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_NOT_FOUND));
		
		
		// 1.. Check place attributes
		// ============================================
		internalSyncAttr(dbPlace, requestPlace);
		
		
		// 2.. Check place HP
		// ============================================
		boolean placeToBeDestroyed = internalSyncPlaceHealth(dbPlace, requestPlace);
		
		if (placeToBeDestroyed) {
			
			// destroy the place
			destroyPlace(dbPlace.getCode());

			// Retrieve it again from the database.
			response = getPlace(placeId);
			
		} else {

			// 3.. Check place class
			// ============================================
			
			// if placeClass is changed, resync the attributes of changed place
			if (!dbPlace.getPlaceClass().getCode().equals(requestPlace.getClassCode())) {
	
				// change placeClass			
				internalUpdateClass(dbPlace, requestPlace.getClassCode());
				
			}
			
			// 4.. Check place exits
			// ============================================
			
			internalSyncExits(dbPlace, requestPlace);
	
			// updating the place in database
			// Mounting the response
			response = PlaceConverter.convert(
					placeRepository.save(dbPlace)
					);
		}
		
		return response;
	}
	
	/**
	 * Check place health attribute and determine when a place is about to be destroyed.
	 * 
	 * This check is performed since the current database place has a MAXHP attribute.
	 * If that happens, the HP attribute is checked in the service request.
	 * 
	 * If the HP attribute value is higher than the MAXHP attribute, the HP will be updated to the maximum allowed.
	 * If the HP attribute value is lower than zero, the place is marked for destruction
	 * 
	 * If the HP attribute is not found in service request, it's considered zero (0).
	 * 
	 * 
	 * @param dbPlace - database representation of the place object
	 * @param requestPlace - service request
	 * @return
	 */
	private boolean internalSyncPlaceHealth(final MudPlace dbPlace, final Place requestPlace) {

		boolean placeDestroyed = false;
		
		// Check current place health
		// First, we obtain the maxHP for this place
		// if this value is different from zero, it means that this is a place that can be destroyed
		Integer maxHP = 
				dbPlace.getAttrs().stream()
					.filter(d-> d.getId().getCode().equals(WorldHelper.PLACE_MAX_HP_ATTR))
					.mapToInt(MudPlaceAttr::getValue)
					.findFirst()
					.orElse(0);
		
		// Retrieve the current HP of the place.  That value came from the request
		Integer currentHP = requestPlace.getAttrs().getOrDefault(WorldHelper.PLACE_HP_ATTR, 0);
		
		// If the currentPlace has an HP and it is exhausted		
		placeDestroyed = (maxHP!=0) && (currentHP <=0);
		
		if ((maxHP!=0) && (currentHP > maxHP)) {
			
			// Adjusts the currentHP to the maximum			
			dbPlace.getAttrs().stream()
				.filter(d -> d.getId().getCode().equals(WorldHelper.PLACE_HP_ATTR))
				.findFirst()
				.ifPresent(e -> e.setValue(maxHP));
		}
		
		return placeDestroyed;
	}
	
	/**
	 * Sync the place attributes according to placeClass changes.
	 * Attributes found in previous class and NOT in the new place class will be removed.
	 * Attributes in the new place class that doesn't exist in current place will be added.
	 * Attributes in the new place class that EXISTS in the current place will be updated.
	 * 
	 * @param dbPlace - database record representing the place object
	 * @param previousPlaceClass - previous place class (null during place creation)
	 * @param placeClass - new place class to be applied
	 * @return
	 */
	private MudPlace internalSyncAttr(MudPlace dbPlace, MudPlaceClass previousPlaceClass, MudPlaceClass placeClass) {
		
		if (previousPlaceClass!=null) {
			
			// Check all the attributes that existed in old class
			// and not exists in the new one
			
			dbPlace.getAttrs().removeIf(d -> {
				
				boolean existsInOldClass = previousPlaceClass.getAttrs().stream()
						.anyMatch(e -> e.getCode().equals(d.getCode()));
				
				boolean existsInNewClass = placeClass.getAttrs().stream()
						.anyMatch(e -> e.getCode().equals(d.getCode()));
			
				return existsInOldClass && !existsInNewClass;
			});
		}
		
		// Looking for attributes to add/update
		placeClass.getAttrs().stream()
			.forEach(curClassAttr -> {
				
				MudPlaceAttr dbAttr = 
					dbPlace.getAttrs().stream()
						.filter(e -> e.getCode().equals(curClassAttr.getCode()))
						.findFirst()
						.orElse(MudPlaceAttrConverter.convert(dbPlace.getCode(), curClassAttr));
				
				// Set the value regardless if the attr came from existing
				// list or was created now
				dbAttr.setValue(curClassAttr.getValue());
				
				// Update the attribute list in entity
				dbPlace.getAttrs().add(dbAttr);
		});

		return dbPlace;
	}
	
	/**
	 * Sync place attributes between the database record and the service request.
	 * Any attributes in database record not found in the request will be removed.
	 * Attributes non existent in database record but in request will be added.
	 * Attributes found in both database and request will be updated.
	 * 
	 * Note: NO persist operation will be performed by this operation
	 * (it only works on entity)
	 * 
	 * @param dbPlace - database record for the place
	 * @param requestPlace - service request place
	 * @return
	 */
	private MudPlace internalSyncAttr(final MudPlace dbPlace, final Place requestPlace) {
		
		// Looking for attributes to remove
		Set<MudPlaceAttr> filteredSet =
			dbPlace.getAttrs().stream()
				// Filtering all database attributes...
				.filter(db -> requestPlace.getAttrs().keySet().stream()
						// ... that isn't in the request
						.noneMatch(req -> req.equals(db.getId().getCode())))
				.collect(Collectors.toSet());

		
		dbPlace.getAttrs().removeAll(filteredSet);
			

		// Looking for attributes to add/update
		for(String curAttr: requestPlace.getAttrs().keySet()) {
			
			Integer curValue = requestPlace.getAttrs().get(curAttr);
			
			// Looking for existing attribute in db record list
			Optional<MudPlaceAttr> foundAttr = 
				dbPlace.getAttrs().stream()
					.filter(a -> a.getId().getCode().equals(curAttr))
					.findFirst();

			// If the value exists in db record
			if (foundAttr.isPresent()) {
				
				// Updates the value of existing attribute
				foundAttr.get().setValue(curValue);
			} else {
				
				// Creates a new attribute
				dbPlace.getAttrs().add(
						MudPlaceAttrConverter.build(dbPlace.getCode(), curAttr, curValue)
						);
			}
		}

		return dbPlace;
	}
	
	private MudPlace internalSyncExits(MudPlace dbPlace, Place requestPlace) {
		
		// 4. exits		
		if (requestPlace.getExits()!=null) {
			
			Set<MudPlaceExit> newExits = new HashSet<>();
			
			requestPlace.getExits().keySet().stream()
				.forEach(curDirection -> {

				// Retrieve the exit from the request
				PlaceExit curRequestExit = requestPlace.getExits().get(curDirection);
					
				// Search the exit in current db record
				MudPlaceExit dbExit = 
					dbPlace.getExits().stream()
						.filter(e -> e.getPk().getDirection().equals(curDirection))
						.findFirst()
						.orElseGet(()-> 
							MudPlaceExitConverter.build(curRequestExit, dbPlace.getCode(), curDirection)
						);
				
				
				// Update the record with request information
				dbExit.setVisible(curRequestExit.isVisible());
				dbExit.setOpened(curRequestExit.isOpened());
				dbExit.setLocked(curRequestExit.isLocked());
				
				// Add the updated exit at list
				newExits.add(dbExit);
				
			});
			
			// As hibernate manages the child list returned by him, we must not to create
			// a new list, but to clear the existing one to force DELETE/UPDATE of changed entries
			dbPlace.getExits().clear();
			dbPlace.getExits().addAll(newExits);
			
		}
		
		return dbPlace;
		
	}
	
	/**
	 * This method changes a place class.
	 * A change like this implies in resync the current place attributes with
	 * the ones in the new place class.
	 * 
	 * This method is called from:
	 * - during destroyPlace flow if the current place class has a designed demised place class
	 * - during updatePlace flow if the current place class is changed for any reason
	 * 
	 * @param original - current place object
	 * @param newPlaceClassCode - code of the new placeClass
	 * @return
	 */
	private MudPlace internalUpdateClass(MudPlace original, String newPlaceClassCode) {
		
		MudPlaceClass placeClass = placeClassRepository
				.findById(newPlaceClassCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_CLASS_NOT_FOUND));

		internalSyncAttr(original, original.getPlaceClass(), placeClass);
		original.setPlaceClass(placeClass);
		
		return original;
	}


	public void destroyPlace(Integer placeId) {
		
		MudPlace dbPlace = placeRepository
				.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_NOT_FOUND));

		// If exists a demise place class for this location
		if (dbPlace.getPlaceClass().getDemisedPlaceClassCode()!=null) {

			// Change the placeClass to the demised one
			internalUpdateClass(dbPlace, dbPlace.getPlaceClass().getDemisedPlaceClassCode());
			
			placeRepository.save(dbPlace);
			
		} else {
			
			// Destroy the place
			placeRepository.deleteById(dbPlace.getCode());
		}
	}


	public Place createPlace(String placeClassCode, String direction, Integer targetPlaceCode) {
		
		Place response = null;
		
		// Retrieving the placeClass
		MudPlaceClass dbPlaceClass = placeClassRepository
				.findById(placeClassCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_CLASS_NOT_FOUND));
		
		// Retrieving the targetPlace
		MudPlace targetDbPlace = placeRepository
				.findById(targetPlaceCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.PLACE_NOT_FOUND));
		
		// Check the corresponding exit of target place to be update in this flow
		String correspondingDirection = PlaceExit.getOpposedDirection(direction);

		// Check if the target place already has an exit to this direction
		if (targetDbPlace.getExits().stream()
			.anyMatch(d -> d.getPk().getDirection().equals(correspondingDirection))) {
			
			throw new IllegalParameterException(LocalizedMessages.PLACE_EXIT_EXISTS);
		}
		
		MudPlace newPlace = new MudPlace();
		newPlace.setPlaceClass(dbPlaceClass);

		// Saving in database with minimum information in order to have the placeId
		MudPlace dbPlace = placeRepository.save(newPlace);
		
		// Updating attributes based on PlaceClass attributes
		internalSyncAttr(dbPlace, null, dbPlaceClass);

		// Creating the exit for the new place
		dbPlace.getExits().add(
				MudPlaceExitConverter.build(
						dbPlace.getCode(), 
						direction, 
						targetPlaceCode)
				);
		
		// Updating the new place in database
		dbPlace = placeRepository.save(dbPlace);
		
		// Updating the targetPlace exit to have a corresponding exit to new place created
		MudPlaceExit correspondingExit = MudPlaceExitConverter.build(
				targetDbPlace.getCode(), 
				correspondingDirection, 
				dbPlace.getCode());
		
		targetDbPlace.getExits().add(correspondingExit);
		placeRepository.save(targetDbPlace);
		
		// Converting the response to service-like response
		response = PlaceConverter.convert(dbPlace);
		
		return response;
	}	
}
