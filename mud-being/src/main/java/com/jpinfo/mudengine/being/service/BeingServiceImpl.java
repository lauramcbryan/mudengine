package com.jpinfo.mudengine.being.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.being.model.MudBeing;
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingClass;
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
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Service
public class BeingServiceImpl {
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private BeingClassRepository classRepository;

	public Being getBeing(Long beingCode) {
		
		return repository
				.findById(beingCode)
				.map(BeingConverter::convert)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
	}
	
	public Being updateBeing(Long beingCode, Being requestBeing) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));
		
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
			
			response = BeingConverter.convert(changedBeing);
		}
		
		return response;
	}
	
	
	public Being createBeing( 
			Being.enumBeingType beingType, String beingClass, String worldName, 
			Integer placeCode, Integer quantity,
			String beingName) {
		
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
		return BeingConverter.convert(dbBeing);
	}

	public Being createPlayerBeing(
			Long playerId, String beingClass, 
			String worldName, Integer placeCode, String beingName) {

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
		return BeingConverter.convert(dbBeing);
	}
	
	public List<Being> getAllFromPlayer(Long playerId) {
		
		return repository.findByPlayerId(playerId).stream()
				.map(BeingConverter::convert)
				.collect(Collectors.toList());
	}

	public List<Being> getAllFromPlace(String worldName, Integer placeCode) {

		return repository.findByCurWorldAndCurPlaceCode(worldName, placeCode).stream()
				.map(BeingConverter::convert)
				.collect(Collectors.toList());
	}

	public void destroyBeing(Long beingCode) {
		
		repository.deleteById(beingCode);
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
}
