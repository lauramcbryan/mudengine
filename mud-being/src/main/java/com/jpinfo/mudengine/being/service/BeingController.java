package com.jpinfo.mudengine.being.service;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

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
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.security.TokenService;
import com.jpinfo.mudengine.common.service.BeingService;

@RestController
public class BeingController implements BeingService {
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private BeingRepository repository;
	
	@Autowired
	private BeingClassRepository classRepository;

	@Override
	public Being getBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Long beingCode) {
		
		Being response = null;
		
		
		MudBeing dbBeing = repository.findOne(beingCode);
		
		if (dbBeing!=null) {
			
			boolean fullResponse = canAccess(authToken, dbBeing.getPlayerId());
			
			response = BeingHelper.buildBeing(dbBeing, fullResponse);
			
			response = expandBeingEquipment(response, dbBeing, fullResponse);
			
		} else {
			throw new EntityNotFoundException("Being entity not found");
		}
		
		return response;
	}
	
	@Override
	public Being updateBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Long beingCode, @RequestBody Being requestBeing) {
		
		Being response = null;
		
		MudBeing dbBeing = repository.findOne(beingCode);
		
		if (dbBeing!=null) {
			
			if (canAccess(authToken, dbBeing.getPlayerId())) {
		
				// Basic data
				dbBeing.setName(requestBeing.getName());
				dbBeing.setPlayerId(requestBeing.getPlayerId());
				dbBeing.setCurPlaceCode(requestBeing.getCurPlaceCode());
				dbBeing.setCurWorld(requestBeing.getCurWorld());
				dbBeing.setQuantity(requestBeing.getQuantity());
				dbBeing.setBeingType(requestBeing.getBeingType());
				
			
				// if the beingClass is changing, reset the attributes
				if (!dbBeing.getBeingClass().equals(requestBeing.getBeingClass())) {
					
					MudBeingClass dbClassBeing = classRepository.findOne(requestBeing.getBeingClass());
					
					if (dbClassBeing!=null) {
	
						dbBeing = BeingHelper.updateBeingClass(dbBeing, dbBeing.getBeingClass(), dbClassBeing);
						
					} else {
						throw new EntityNotFoundException("Being Class entity not found");
					}
					
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
				throw new IllegalParameterException("No access to that being");
			}
			
		} else {
			throw new EntityNotFoundException("Being entity not found"); 
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Being> createBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@RequestParam Integer beingType, @RequestParam String beingClass, @RequestParam String worldName, 
			@RequestParam Integer placeCode, @RequestParam Optional<Integer> quantity, @RequestParam Optional<Long> playerId) {
		
		ResponseEntity<Being> entityResponse = null; 

		// Checking the playerId against the authenticated playerId
		if ((!playerId.isPresent()) || canAccess(authToken, playerId.get())) {
		
			MudBeing dbBeing = new MudBeing();
			
			MudBeingClass dbBeingClass = classRepository.findOne(beingClass);
	
			dbBeing.setBeingType(beingType);
			dbBeing.setBeingClass(dbBeingClass);
			dbBeing.setCurPlaceCode(placeCode);
			dbBeing.setCurWorld(worldName);
			
			if (quantity.isPresent())
				dbBeing.setQuantity(quantity.get());
			else
				dbBeing.setQuantity(1);
	
			if (playerId.isPresent())
				dbBeing.setPlayerId(playerId.get());
			
			// Saving the entity (to have the beingCode)
			dbBeing = repository.save(dbBeing);
			
			
			// 2. attributes  (from class)
			// 3. skills  (from class)	
			dbBeing = BeingHelper.updateBeingClass(dbBeing, null, dbBeingClass);
			
			dbBeing = repository.save(dbBeing);
			
			// Convert to the response
			Being response = BeingHelper.buildBeing(dbBeing, true);
			
			entityResponse = new ResponseEntity<Being>(response, HttpStatus.CREATED);
			
		} else {
			throw new IllegalParameterException("Cannot create beings for another player");
		}
		
		return entityResponse;
	}
	
	@Override
	public List<Being> getAllFromPlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Long playerId) {
		
		List<Being> response = null;
		
		if (canAccess(authToken, playerId)) {
		
			List<MudBeing> lstFound = repository.findByPlayerId(playerId);
			
			response = new ArrayList<Being>();
			
			for(MudBeing curDbBeing: lstFound) {
				response.add(BeingHelper.buildBeing(curDbBeing, false));
			}
		} else {
			throw new IllegalParameterException("No access to that being");
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
	public void destroyBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Long beingCode) {
		
		MudBeing dbBeing = repository.findOne(beingCode);
		
		if (dbBeing!=null) {
			
			if (canAccess(authToken, dbBeing.getPlayerId())) {
			
				// Update Item service to drop all items of this being
				itemService.dropAllFromBeing(beingCode, dbBeing.getCurWorld(), dbBeing.getCurPlaceCode());
				
				repository.delete(beingCode);
			} else {
				throw new IllegalParameterException("No access to that being");
			}
			
		} else {
			throw new EntityNotFoundException("Being entity not found");
		}
	}
	
	private Being expandBeingEquipment(Being responseBeing, MudBeing dbBeing, boolean fullResponse) {
		
		for(MudBeingSlot curSlot: dbBeing.getEquipment()) {
			
			Item responseItem = null;
			
			if (curSlot.getItemCode()!=null) {
				responseItem = itemService.getItem(curSlot.getItemCode());
			} else {
				responseItem = new Item();
				responseItem.setItemClass("NOTHING");
			}
			
			responseBeing.getEquipment().put(curSlot.getId().getSlotCode(), responseItem);
		}
		
		return responseBeing;
	}

	@Override
	public void destroyAllFromPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudBeing> lstFound = repository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(curDbBeing.getBeingCode(), worldName, placeCode);
			
			repository.delete(curDbBeing);
		}
	}
	
	@Override
	public void destroyAllFromPlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable Long playerId) {
		
		List<MudBeing> lstFound = repository.findByPlayerId(playerId);
		
		for(MudBeing curDbBeing: lstFound) {
			
			itemService.dropAllFromBeing(curDbBeing.getBeingCode(), curDbBeing.getCurWorld(), curDbBeing.getCurPlaceCode());
			
			repository.delete(curDbBeing);
		}
	}
	
	private boolean canAccess(String authToken, Long playerId) {
		
		Long authPlayerId = TokenService.getPlayerIdFromToken(authToken);
		
		return ((playerId==null) || (playerId.equals(authPlayerId)) || (TokenService.INTERNAL_PLAYER_ID.equals(authPlayerId)));
		
	}
	
}
