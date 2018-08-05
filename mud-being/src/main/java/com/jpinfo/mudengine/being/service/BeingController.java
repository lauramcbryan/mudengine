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
import com.jpinfo.mudengine.being.model.MudBeingAttr;
import com.jpinfo.mudengine.being.model.MudBeingAttrModifier;
import com.jpinfo.mudengine.being.model.MudBeingClass;
import com.jpinfo.mudengine.being.model.MudBeingClassAttr;
import com.jpinfo.mudengine.being.model.MudBeingClassSkill;
import com.jpinfo.mudengine.being.model.MudBeingClassSlot;
import com.jpinfo.mudengine.being.model.MudBeingSkill;
import com.jpinfo.mudengine.being.model.MudBeingSkillModifier;
import com.jpinfo.mudengine.being.model.MudBeingSlot;
import com.jpinfo.mudengine.being.model.converter.BeingConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingAttrConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingSkillConverter;
import com.jpinfo.mudengine.being.model.converter.MudBeingSlotConverter;
import com.jpinfo.mudengine.being.model.pk.MudBeingAttrModifierPK;
import com.jpinfo.mudengine.being.model.pk.MudBeingSkillModifierPK;
import com.jpinfo.mudengine.being.repository.BeingClassRepository;
import com.jpinfo.mudengine.being.repository.BeingRepository;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;
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
		
		response = BeingConverter.convert(dbBeing, fullResponse);
		
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
			dbBeing.setCurPlaceCode(requestBeing.getCurPlaceCode());
			dbBeing.setCurWorld(requestBeing.getCurWorld());
			dbBeing.setQuantity(requestBeing.getQuantity());
			
			// 2. attrModifiers
			updateBeingAttrModifiers(dbBeing, requestBeing);
			
			// 3. skillModifiers
			updateBeingSkillModifiers(dbBeing, requestBeing);

		
			// if the beingClass is changing, reset the attributes
			if (!dbBeing.getBeingClass().getBeingClassCode().equals(requestBeing.getBeingClassCode())) {
				
				MudBeingClass dbClassBeing = classRepository
						.findById(requestBeing.getBeingClassCode())
						.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_CLASS_NOT_FOUND));
				
				dbBeing = updateBeingClass(dbBeing, dbBeing.getBeingClass(), dbClassBeing);
				
				dbBeing.setBeingClass(dbClassBeing);
			}
						
			// Updating the entity
			MudBeing changedBeing = repository.save(dbBeing);
			
			response = BeingConverter.convert(changedBeing, true);
		} else {
			throw new AccessDeniedException(LocalizedMessages.BEING_ACCESS_DENIED);
		}
		
		return response;
	}

	private MudBeing updateBeingAttrModifiers(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getAttrModifiers()!=null) {
			
			List<MudBeingAttrModifier> attrList = new ArrayList<>();
			
			for(BeingAttrModifier curAttrModifier: requestBeing.getAttrModifiers()) {
				
				MudBeingAttrModifier newDbAttrModifier = new MudBeingAttrModifier();
				MudBeingAttrModifierPK newDbAttrModifierPK = new MudBeingAttrModifierPK();
				
				newDbAttrModifierPK.setAttrCode(curAttrModifier.getAttribute());
				newDbAttrModifierPK.setBeingCode(dbBeing.getBeingCode());
				newDbAttrModifierPK.setOriginCode(curAttrModifier.getOriginCode());
				newDbAttrModifierPK.setOriginType(curAttrModifier.getOriginType());
				
				newDbAttrModifier.setId(newDbAttrModifierPK);
				newDbAttrModifier.setOffset(curAttrModifier.getOffset());
				newDbAttrModifier.setEndTurn(curAttrModifier.getEndTurn());
				
				attrList.add(newDbAttrModifier);
			}

			dbBeing.getAttrModifiers().clear();
			dbBeing.getAttrModifiers().addAll(attrList);
		}
				
		return dbBeing;
	}
	
	
	private MudBeing updateBeingSkillModifiers(MudBeing dbBeing, Being requestBeing) {
		
		if (requestBeing.getSkillModifiers()!=null) {
			
			List<MudBeingSkillModifier> skillList = new ArrayList<>();
			
			for(BeingSkillModifier curSkillModifier: requestBeing.getSkillModifiers()) {
				
				MudBeingSkillModifier newDbSkillModifier = new MudBeingSkillModifier();
				MudBeingSkillModifierPK newDbSkillModifierPK = new MudBeingSkillModifierPK();
				
				newDbSkillModifierPK.setBeingCode(dbBeing.getBeingCode());
				newDbSkillModifierPK.setSkillCode(curSkillModifier.getSkillCode());
				newDbSkillModifierPK.setOriginCode(curSkillModifier.getOriginCode());
				newDbSkillModifierPK.setOriginType(curSkillModifier.getOriginType());
				
				newDbSkillModifier.setId(newDbSkillModifierPK);
				newDbSkillModifier.setOffset(curSkillModifier.getOffset());
				newDbSkillModifier.setEndTurn(curSkillModifier.getEndTurn());
				
				skillList.add(newDbSkillModifier);
			}

			dbBeing.getSkillModifiers().clear();
			dbBeing.getSkillModifiers().addAll(skillList);
		}
		
		return dbBeing;
	}
	
	private MudBeing updateBeingAttributes(MudBeing dbBeing, Being requestBeing) {

		// Looking for attributes to remove
		for(MudBeingAttr curItemAttr: dbBeing.getAttrs()) {
			
			// If it not exists in request, remove it
			if (requestBeing.getAttrs().get(curItemAttr.getId().getAttrCode())==null) {
				dbBeing.getAttrs().remove(curItemAttr);
			}
		}

		// Looking for attributes to add
		for(String curAttr: requestBeing.getAttrs().keySet()) {
			
			MudBeingAttr newAttr = MudBeingAttrConverter.build(dbBeing.getBeingCode(), 
					curAttr, 
					requestBeing.getAttrs().get(curAttr));
			
			if (!dbBeing.getAttrs().contains(newAttr)) {
				dbBeing.getAttrs().add(newAttr);
			}
		}
		
		return dbBeing;
	}
	
	private MudBeing updateBeingClass(MudBeing dbBeing, MudBeingClass previousClass, MudBeingClass beingClass) {
		
		if (previousClass!=null) {
			
			// Removing attributes set by previous being class
			// (attributes modifiers aren't changed)
			for (MudBeingClassAttr curAttr: previousClass.getAttributes()) {
				
				MudBeingAttr oldAttr = MudBeingAttrConverter.build(dbBeing.getBeingCode(), curAttr.getId().getAttrCode(), curAttr.getAttrValue());
				
				dbBeing.getAttrs().remove(oldAttr);
			}
			
			// Removing skills set by previous being class
			// (skills modifiers aren't changed)
			for (MudBeingClassSkill curSkill: previousClass.getSkills()) {
				
				MudBeingSkill oldSkill = MudBeingSkillConverter.build(dbBeing.getBeingCode(), curSkill.getId().getSkillCode(), curSkill.getSkillValue());
				
				dbBeing.getSkills().remove(oldSkill);
			}
			
			// Removing slots set by previous being class
			for (MudBeingClassSlot curSlot: previousClass.getSlots()) {
				
				MudBeingSlot oldSlot = MudBeingSlotConverter.build(dbBeing.getBeingCode(), curSlot.getId().getSlotCode());
				
				dbBeing.getEquipment().remove(oldSlot);
			}
			
		}
		
		// Adding attributes from new beingClass
		for(MudBeingClassAttr curAttr: beingClass.getAttributes()) {
			
			MudBeingAttr newAttr = MudBeingAttrConverter.build(dbBeing.getBeingCode(), curAttr.getId().getAttrCode(), curAttr.getAttrValue());
			
			if (!dbBeing.getAttrs().contains(newAttr)) {
				dbBeing.getAttrs().add(newAttr);
			}
		}
		
		// Adding skills from new beingClass
		for (MudBeingClassSkill curSkill: beingClass.getSkills()) {
			
			MudBeingSkill newSkill = MudBeingSkillConverter.build(dbBeing.getBeingCode(), curSkill.getId().getSkillCode(), curSkill.getSkillValue());
			
			if (!dbBeing.getSkills().contains(newSkill)) {
				dbBeing.getSkills().add(newSkill);
			}
		}
		
		// Adding slots from new beingClass
		for (MudBeingClassSlot curSlot: beingClass.getSlots()) {
		
			MudBeingSlot newSlot = MudBeingSlotConverter.build(dbBeing.getBeingCode(), curSlot.getId().getSlotCode());
			
			if (!dbBeing.getEquipment().contains(newSlot)) {
				dbBeing.getEquipment().add(newSlot);
			}
		}
		
		return dbBeing;
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
		dbBeing = updateBeingClass(dbBeing, null, dbBeingClass);
		
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
		dbBeing = updateBeingClass(dbBeing, null, dbBeingClass);
		
		dbBeing = repository.save(dbBeing);
		
		// Convert to the response
		Being response = BeingConverter.convert(dbBeing, true);
		
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
				response.add(BeingConverter.convert(curDbBeing, false));
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
			response.add(BeingConverter.convert(curDbBeing, false));
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
