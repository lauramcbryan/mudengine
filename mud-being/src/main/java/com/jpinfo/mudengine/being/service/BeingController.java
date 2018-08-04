package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.client.ItemServiceClient;
import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.being.utils.BeingHelper;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.exception.AccessDeniedException;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.player.Player;
import com.jpinfo.mudengine.common.security.MudUserDetails;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.BeingService;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@RestController
public class BeingController implements BeingService {
	
	private static final Logger log = LoggerFactory.getLogger(BeingController.class);
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private BeingClassRepository classRepository;

	@Override
	public Being getBeing(@PathVariable Long beingCode) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
		boolean fullResponse = canAccess(dbBeing.getPlayerId());
		
		response = BeingHelper.buildBeing(dbBeing, fullResponse);
		
		return expandBeingEquipment(response, dbBeing, fullResponse);
	}
	
	@Override
	public Being updateBeing(@PathVariable Long beingCode, @RequestBody Being requestBeing) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
		if (canAccess(dbBeing.getPlayerId())) {
	
			// Basic data
			dbBeing.setName(requestBeing.getName());
			dbBeing.setPlayerId(requestBeing.getPlayerId());
			dbBeing.setCurPlaceCode(requestBeing.getCurPlaceCode());
			dbBeing.setCurWorld(requestBeing.getCurWorld());
			dbBeing.setQuantity(requestBeing.getQuantity());
			dbBeing.setBeingType(requestBeing.getBeingType());
			
		
			// if the beingClass is changing, reset the attributes
			if (!dbBeing.getBeingClass().getBeingClassCode().equals(requestBeing.getBeingClassCode())) {
				
				MudBeingClass dbClassBeing = classRepository
						.findById(requestBeing.getBeingClassCode())
						.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));
				
				dbBeing = BeingHelper.updateBeingClass(dbBeing, dbBeing.getBeingClass(), dbClassBeing);
				
				dbBeing.setBeingClass(dbClassBeing);
			}
			
			// 2. attrModifiers
			dbBeing = BeingHelper.updateBeingAttrModifiers(dbBeing, requestBeing);
			
			// 3. skillModifiers
			dbBeing = BeingHelper.updateBeingSkillModifiers(dbBeing, requestBeing);
			
			// Updating the entity
			MudBeing changedBeing = repository.save(dbBeing);
			
			response = BeingHelper.buildBeing(changedBeing, true);
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Being> createBeing( 
			@RequestParam Integer beingType, @RequestParam String beingClass, @RequestParam String worldName, 
			@RequestParam Integer placeCode, @RequestParam Integer quantity,
			@RequestParam String beingName) {
		
		ResponseEntity<Being> entityResponse = null; 

		MudBeing dbBeing = new MudBeing();
		
		MudBeingClass dbBeingClass = classRepository
				.findById(beingClass)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));

		dbBeing.setBeingType(beingType);
		dbBeing.setBeingClass(dbBeingClass);
		dbBeing.setCurPlaceCode(placeCode);
		dbBeing.setCurWorld(worldName);
		
		if ((beingName!=null) && (!beingName.trim().isEmpty()))
			dbBeing.setName(beingName);
		
		if (quantity!=null)
			dbBeing.setQuantity(quantity);
		else
			dbBeing.setQuantity(1);
		
		// Saving the entity (to have the beingCode)
		dbBeing = repository.save(dbBeing);
			
			
		// 2. attributes  (from class)
		// 3. skills  (from class)	
		dbBeing = BeingHelper.updateBeingClass(dbBeing, null, dbBeingClass);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingHelper.buildBeing(dbBeing, true);
		
		entityResponse = new ResponseEntity<>(response, HttpStatus.CREATED);
		
		return entityResponse;
	}

	@Override
	public ResponseEntity<Being> createPlayerBeing(
			@PathVariable Long playerId, @RequestParam String beingClass, 
			@RequestParam String worldName, @RequestParam Integer placeCode, @RequestParam String beingName) {
		
		ResponseEntity<Being> entityResponse = null;

		// Checking the name's availability
		if ((beingName!=null) && (repository.findByName(beingName).isPresent())) {
			throw new IllegalParameterException(LocalizedMessages.BEING_NAME_IN_USE);
		}

		// Checking the playerId against the authenticated playerId
		MudBeing dbBeing = new MudBeing();
		
		MudBeingClass dbBeingClass = classRepository
				.findById(beingClass)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));

		dbBeing.setBeingType(Being.BEING_TYPE_PLAYER);
		dbBeing.setBeingClass(dbBeingClass);
		dbBeing.setCurPlaceCode(placeCode);
		dbBeing.setCurWorld(worldName);
		dbBeing.setPlayerId(playerId);
		dbBeing.setName(beingName);
		dbBeing.setQuantity(1);
		
		// Saving the entity (to have the beingCode)
		dbBeing = repository.save(dbBeing);
			
			
		// 2. attributes  (from class)
		// 3. skills  (from class)	
		dbBeing = BeingHelper.updateBeingClass(dbBeing, null, dbBeingClass);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingHelper.buildBeing(dbBeing, true);
		
		entityResponse = new ResponseEntity<>(response, HttpStatus.CREATED);
		
		return entityResponse;
	}
	
	@Override
	public List<Being> getAllFromPlayer(@PathVariable Long playerId) {
		
		List<Being> response = null;
		
		if (canAccess(playerId)) {
		
			List<MudBeing> lstFound = repository.findByPlayerId(playerId);
			
			response = new ArrayList<>();
			
			for(MudBeing curDbBeing: lstFound) {
				response.add(BeingHelper.buildBeing(curDbBeing, false));
			}
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
		
		return response;
	}

	@Override
	public List<Being> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {

		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		List<Being> response = new ArrayList<>();
		
		for(MudBeing curDbBeing: lstFound) {
			response.add(BeingHelper.buildBeing(curDbBeing, false));
		}
		
		return response;
	}

	@Override
	public void destroyBeing(@PathVariable Long beingCode) {
		
		MudBeing dbBeing = repository.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
		if (canAccess(dbBeing.getPlayerId())) {
			
			try {
		
				// Update Item service to drop all items of this being
				itemService.dropAllFromBeing(beingCode, dbBeing.getCurWorld(), dbBeing.getCurPlaceCode());
			} catch(Exception e) {
				
				// as this is a *should have* feature, errors at this point
				// are being disregarded and just logged.
				log.error("Error while cascading destroyBeing to dropAllFromBeing", e);
				
			}
			
			repository.deleteById(beingCode);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
	}
	
	private Being expandBeingEquipment(Being responseBeing, MudBeing dbBeing, boolean fullResponse) {
		
		for(MudBeingSlot curSlot: dbBeing.getEquipment()) {
			
			Item responseItem = null;
			
			if (curSlot.getItemCode()!=null) {
				responseItem = itemService.getItem(curSlot.getItemCode());
			} else {
				responseItem = new Item();
				responseItem.setItemClassCode("NOTHING");
			}
			
			responseBeing.getEquipment().put(curSlot.getId().getSlotCode(), responseItem);
		}
		
		return responseBeing;
	}

	@Override
	public void destroyAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(curDbBeing.getBeingCode(), worldName, placeCode);
			
			repository.delete(curDbBeing);
		}
	}
	
	@Override
	public void destroyAllFromPlayer(@PathVariable Long playerId) {
		
		List<MudBeing> lstFound = repository.findByPlayerId(playerId);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(curDbBeing.getBeingCode(), curDbBeing.getCurWorld(), curDbBeing.getCurPlaceCode());
			
			repository.delete(curDbBeing);
		}
	}
	
	private boolean canAccess(Long playerId) {
		
		MudUserDetails uDetails = (MudUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		boolean allowed = false;
		
		Optional<Player> playerData = uDetails.getPlayerData();
		
		if (playerData.isPresent()) {
			
			Long authPlayerId = playerData.get().getPlayerId();
			
			allowed = authPlayerId.equals(playerId) || authPlayerId.equals(TokenService.INTERNAL_PLAYER_ID); 
			
		}
		
		return allowed;
		
	}
}
