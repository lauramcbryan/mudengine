package com.jpinfo.mudengine.being.service;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

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
import com.jpinfo.mudengine.being.model.MudBeingAttr;
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
			if (requestBeing.getType().equals(Being.enumBeingType.REGULAR_NON_SENTIENT) ||
					(requestBeing.getType().equals(Being.enumBeingType.REGULAR_SENTIENT))) {
			
				dbBeing.setQuantity(requestBeing.getQuantity());
			}

			// 2. attributes
			MudBeingAttrConverter.sync(dbBeing, requestBeing);
			
			// Checking HP attributes
			boolean beingToBeDestroyed = internalSyncBeingHP(dbBeing, requestBeing);
			
			MudBeing changedBeing = null;
			
			if (beingToBeDestroyed) {
				destroyBeing(dbBeing.getCode());
			} else {
				
				// 3. skills
				MudBeingSkillConverter.sync(dbBeing, requestBeing);
				
				// 4. attrModifiers
				MudBeingAttrModifierConverter.sync(dbBeing, requestBeing);
				
				// 5. skillModifiers
				MudBeingSkillModifierConverter.sync(dbBeing, requestBeing);
	
			
				// if the beingClass is changing, reset the attributes
				if (!dbBeing.getBeingClass().getCode().equals(requestBeing.getClassCode())) {
					
					MudBeingClass dbClassBeing = classRepository
							.findById(requestBeing.getClassCode())
							.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));
					
					updateBeingClass(dbBeing, dbBeing.getBeingClass(), dbClassBeing);
				}
							
				// Updating the entity
				changedBeing = repository.save(dbBeing);
				
				response = BeingConverter.convert(changedBeing, true);
			}
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
		
		return response;
	}
	
	
	@Override
	public ResponseEntity<Being> createBeing( 
			@RequestParam Being.enumBeingType beingType, @RequestParam String beingClass, @RequestParam String worldName, 
			@RequestParam Integer placeCode, @RequestParam Integer quantity,
			@RequestParam String beingName) {
		
		ResponseEntity<Being> entityResponse = null; 

		MudBeing dbBeing = new MudBeing();
		
		MudBeingClass dbBeingClass = classRepository
				.findById(beingClass)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));

		dbBeing.setType(beingType.ordinal());
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

		dbBeing.setType(Being.enumBeingType.PLAYABLE.ordinal());
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
		
		return lstFound.stream()
				.map(BeingConverter::convert)
				.collect(Collectors.toList());
	}

	@Override
	public void destroyBeing(@PathVariable Long beingCode) {
		
		MudBeing dbBeing = repository.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
		if (canAccess(dbBeing.getPlayerId())) {

			// Initially we had a call to itemService here to drop all being items.
			// Currently this is done asynchronously through AOP pointcut
			repository.delete(dbBeing);
			
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
	}
	
	private boolean internalSyncBeingHP(MudBeing dbBeing, Being requestBeing) {
		
		boolean beingDestroyed = false;
		
		// Check current being health
		// First, we obtain the max HP for this being
		// if this value is different from zero, it means that this is a being that can be destroyed
		Integer maxHP = 
				dbBeing.getAttrs().stream()
					.filter(d-> d.getCode().equals(BeingHelper.BEING_MAX_HP_ATTR))
					.map(MudBeingAttr::getValue)
					.findFirst()
					.orElse(0);
		
		// Retrieve the current HP of the being.  That value came from the request
		Integer currentHP = requestBeing.getAttrs().getOrDefault(BeingHelper.BEING_HP_ATTR, 0);
		
		// If the currentBeing has a HP and it is exhausted		
		beingDestroyed = (maxHP!=0) && (currentHP<=0);
		
		// Checks if the current duration is greater than maximum
		if ((maxHP!=0) && (currentHP > maxHP)) {
			
			// Adjusts the current duration to the maximum
			dbBeing.getAttrs().stream()
				.filter(d -> d.getCode().equals(BeingHelper.BEING_HP_ATTR))
				.findFirst()
				.ifPresent(e -> e.setValue(maxHP));
		}
		
		return beingDestroyed;
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
			
			responseBeing.getEquipment().put(curSlot.getId().getCode(), responseItem);
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
