package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
import com.jpinfo.mudengine.common.service.BeingService;
import com.jpinfo.mudengine.common.utils.CommonConstants;

@RestController
public class BeingController implements BeingService {
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private BeingClassRepository classRepository;

	@Override
	public Being getBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long beingCode) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException("Being entity not found"));
		
		boolean fullResponse = BeingHelper.canAccess(authToken, dbBeing.getPlayerId());
		
		response = BeingHelper.buildBeing(dbBeing, fullResponse);
		
		response = expandBeingEquipment(authToken, response, dbBeing, fullResponse);
		
		return response;
	}
	
	@Override
	public Being updateBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long beingCode, @RequestBody Being requestBeing) {
		
		Being response = null;
		
		MudBeing dbBeing = repository
				.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException("Being entity not found"));
		
		if (BeingHelper.canAccess(authToken, dbBeing.getPlayerId())) {
	
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
						.orElseThrow(() -> new EntityNotFoundException("Being Class not found"));
				
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
			throw new AccessDeniedException("No access to that being");
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Being> createBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, 
			@RequestParam Integer beingType, @RequestParam String beingClass, @RequestParam String worldName, 
			@RequestParam Integer placeCode, @RequestParam Integer quantity,
			@RequestParam String beingName) {
		
		ResponseEntity<Being> entityResponse = null; 

		MudBeing dbBeing = new MudBeing();
		
		MudBeingClass dbBeingClass = classRepository
				.findById(beingClass)
				.orElseThrow(() -> new EntityNotFoundException("Being Class not found"));

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
		
		entityResponse = new ResponseEntity<Being>(response, HttpStatus.CREATED);
		
		return entityResponse;
	}

	@Override
	public ResponseEntity<Being> createPlayerBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken,
			@PathVariable Long playerId, @RequestParam String beingClass, 
			@RequestParam String worldName, @RequestParam Integer placeCode, @RequestParam String beingName) {
		
		ResponseEntity<Being> entityResponse = null;

		// Checking the name's availability
		if ((beingName!=null) && (repository.findByName(beingName).isPresent())) {
			throw new IllegalParameterException("Being name already in use");
		}

		// Checking the playerId against the authenticated playerId
		MudBeing dbBeing = new MudBeing();
		
		MudBeingClass dbBeingClass = classRepository
				.findById(beingClass)
				.orElseThrow(() -> new EntityNotFoundException("Being Class not found"));

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
		
		entityResponse = new ResponseEntity<Being>(response, HttpStatus.CREATED);
		
		return entityResponse;
	}
	
	@Override
	public List<Being> getAllFromPlayer(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long playerId) {
		
		List<Being> response = null;
		
		if (BeingHelper.canAccess(authToken, playerId)) {
		
			List<MudBeing> lstFound = repository.findByPlayerId(playerId);
			
			response = new ArrayList<Being>();
			
			for(MudBeing curDbBeing: lstFound) {
				response.add(BeingHelper.buildBeing(curDbBeing, false));
			}
		} else {
			throw new AccessDeniedException("No access to that being");
		}
		
		return response;
	}

	@Override
	public List<Being> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {

		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		List<Being> response = new ArrayList<Being>();
		
		for(MudBeing curDbBeing: lstFound) {
			response.add(BeingHelper.buildBeing(curDbBeing, false));
		}
		
		return response;
	}

	@Override
	public void destroyBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long beingCode) {
		
		MudBeing dbBeing = repository.findById(beingCode)
				.orElseThrow(() -> new EntityNotFoundException("Being entity not found"));
		
		if (BeingHelper.canAccess(authToken, dbBeing.getPlayerId())) {
			
			try {
		
				// Update Item service to drop all items of this being
				itemService.dropAllFromBeing(authToken, beingCode, dbBeing.getCurWorld(), dbBeing.getCurPlaceCode());
			} catch(Exception e) {
				
				// as this is a *should have* feature, errors at this point
				// are being disregarded and just logged.
				e.printStackTrace(System.err);
				
			}
			
			repository.deleteById(beingCode);
			
			} else {
				throw new AccessDeniedException("No access to that being");
			}
	}
	
	private Being expandBeingEquipment(String token, Being responseBeing, MudBeing dbBeing, boolean fullResponse) {
		
		for(MudBeingSlot curSlot: dbBeing.getEquipment()) {
			
			Item responseItem = null;
			
			if (curSlot.getItemCode()!=null) {
				responseItem = itemService.getItem(token, curSlot.getItemCode());
			} else {
				responseItem = new Item();
				responseItem.setItemClassCode("NOTHING");
			}
			
			responseBeing.getEquipment().put(curSlot.getId().getSlotCode(), responseItem);
		}
		
		return responseBeing;
	}

	@Override
	public void destroyAllFromPlace(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(authToken, curDbBeing.getBeingCode(), worldName, placeCode);
			
			repository.delete(curDbBeing);
		}
	}
	
	@Override
	public void destroyAllFromPlayer(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long playerId) {
		
		List<MudBeing> lstFound = repository.findByPlayerId(playerId);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(authToken, curDbBeing.getBeingCode(), curDbBeing.getCurWorld(), curDbBeing.getCurPlaceCode());
			
			repository.delete(curDbBeing);
		}
	}
}
