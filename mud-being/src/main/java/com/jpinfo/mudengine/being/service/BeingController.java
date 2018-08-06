package com.jpinfo.mudengine.being.service;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

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
import com.jpinfo.mudengine.being.model.converter.BeingConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingAttrConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingAttrModifierConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingSkillConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingSkillModifierConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingSlotConverter;
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
		
		response = BeingConverter.convert(dbBeing, canAccess(dbBeing.getPlayerId()));
		
		return expandBeingEquipment(response, dbBeing);
	}
	
	@Override
	public Being updateBeing(@PathVariable Long beingCode, @RequestBody Being requestBeing) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
		if (canAccess(dbBeing.getPlayerId())) {
	
			// Updating basic fields
			dbBeing.setCurPlaceCode(requestBeing.getCurPlaceCode());
			dbBeing.setCurWorld(requestBeing.getCurWorld());
			
			// We only update quantity for regular beings (non NPC and not player beings)
			if (requestBeing.getBeingType().equals(Being.BEING_TYPE_REGULAR_NON_SENTIENT) ||
					(requestBeing.getBeingType().equals(Being.BEING_TYPE_REGULAR_SENTIENT))) {
			
				dbBeing.setQuantity(requestBeing.getQuantity());
			}
			
			// 2. attributes
			MudBeingAttrConverter.sync(dbBeing, requestBeing);
			
			// 3. skills
			MudBeingSkillConverter.sync(dbBeing, requestBeing);
			
			// 4. attrModifiers
			MudBeingAttrModifierConverter.sync(dbBeing, requestBeing);
			
			// 5. skillModifiers
			MudBeingSkillModifierConverter.sync(dbBeing, requestBeing);

		
			// if the beingClass is changing, reset the attributes
			if (!dbBeing.getBeingClass().getBeingClassCode().equals(requestBeing.getBeingClassCode())) {
				
				MudBeingClass dbClassBeing = classRepository
						.findById(requestBeing.getBeingClassCode())
						.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));
				
				updateBeingClass(dbBeing, dbBeing.getBeingClass(), dbClassBeing);
			}
						
			// Updating the entity
			MudBeing changedBeing = repository.save(dbBeing);
			
			response = BeingConverter.convert(changedBeing, true);
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
			dbBeing.setQuantity(BeingHelper.CREATE_DEFAULT_QUANTITY);
		
		// Saving the entity (to have the beingCode)
		dbBeing = repository.save(dbBeing);
			
			
		// 2. attributes  (from class)
		// 3. skills  (from class)	
		updateBeingClass(dbBeing, null, dbBeingClass);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingConverter.convert(dbBeing, true);
		
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
		dbBeing.setQuantity(BeingHelper.CREATE_DEFAULT_QUANTITY);
		
		// Saving the entity (to have the beingCode)
		dbBeing = repository.save(dbBeing);
			
			
		// 2. attributes  (from class)
		// 3. skills  (from class)	
		updateBeingClass(dbBeing, null, dbBeingClass);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingConverter.convert(dbBeing, true);
		
		entityResponse = new ResponseEntity<>(response, HttpStatus.CREATED);
		
		return entityResponse;
	}
	
	@Override
	public List<Being> getAllFromPlayer(@PathVariable Long playerId) {
		
		List<Being> response;
		
		if (canAccess(playerId)) {
		
			List<MudBeing> lstFound = repository.findByPlayerId(playerId);
			
			response =
				lstFound.stream()
					.map(BeingConverter::convert)
					.collect(Collectors.toList());
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
		
		return response;
	}

	@Override
	public List<Being> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {

		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		List<Being> response;
		
		response = lstFound.stream()
				.map(BeingConverter::convert)
				.collect(Collectors.toList());
		
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
			
			repository.delete(dbBeing);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
	}
	
	@Override
	public void destroyAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		lstFound.stream()
			.forEach(d -> {
				
				itemService.dropAllFromBeing(d.getBeingCode(), worldName, placeCode);
				repository.delete(d);
			});
	}
	
	@Override
	public void destroyAllFromPlayer(@PathVariable Long playerId) {
		
		List<MudBeing> lstFound = repository.findByPlayerId(playerId);
		
		lstFound.stream()
			.forEach(d -> {
				
				// Drop items carried by that being
				itemService.dropAllFromBeing(d.getBeingCode(), d.getCurWorld(), d.getCurPlaceCode());
				
				// Delete the being in database
				repository.delete(d);
			});
	}
	
	private MudBeing updateBeingClass(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass nextClass) {
		
		// Synchronizing attributes with the new being class
		MudBeingAttrConverter.sync(dbBeing, previousClass, nextClass);
		
		// Synchronizing skills with the new being class
		MudBeingSkillConverter.sync(dbBeing, previousClass, nextClass);
		
		// Synchronizing slots with the new being class
		MudBeingSlotConverter.sync(dbBeing, previousClass, nextClass);

		// Synchronizing attribute modifier with the new being class (no addition, only removal)
		MudBeingAttrModifierConverter.sync(dbBeing, previousClass, nextClass);
		
		// Synchronizing skill modifier with the new being class (no addition, only removal)		
		MudBeingSkillModifierConverter.sync(dbBeing, previousClass, nextClass);
		
		// Updating the being class
		dbBeing.setBeingClass(nextClass);
		
		return dbBeing;
	}


	private Being expandBeingEquipment(Being responseBeing, MudBeing dbBeing) {
		
		for(MudBeingSlot curSlot: dbBeing.getSlots()) {
			
			Item responseItem = null;
			
			if (curSlot.getItemCode()!=null) {
				responseItem = itemService.getItem(curSlot.getItemCode());
			}
			
			responseBeing.getEquipment().put(curSlot.getId().getSlotCode(), responseItem);
		}
		
		return responseBeing;
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
