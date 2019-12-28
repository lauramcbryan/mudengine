package com.jpinfo.mudengine.being.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.being.service.BeingServiceImpl;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.service.BeingService;

@RestController
public class BeingController implements BeingService {
	
	@Autowired
	private BeingServiceImpl service;
	
	@Override
	public Being getBeing(@PathVariable Long beingCode) {
		
		return service.getBeing(beingCode);
	}
	
	@Override
	public Being updateBeing(@PathVariable Long beingCode, @RequestBody Being requestBeing) {

		return service.updateBeing(beingCode, requestBeing);
	}
	
	
	@Override
	public ResponseEntity<Being> createBeing( 
			@RequestParam Being.enumBeingType beingType, @RequestParam String beingClass, @RequestParam String worldName, 
			@RequestParam Integer placeCode, @RequestParam Integer quantity,
			@RequestParam String beingName) {
		
		return new ResponseEntity<>(
				service.createBeing(beingType, beingClass, worldName, placeCode, quantity, beingName), 
				HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Being> createPlayerBeing(
			@PathVariable Long playerId, @RequestParam String beingClass, 
			@RequestParam String worldName, @RequestParam Integer placeCode, @RequestParam String beingName) {

		return new ResponseEntity<>(
				service.createPlayerBeing(playerId, beingClass, worldName, placeCode, beingName), 
				HttpStatus.CREATED);
	}
	
	@Override
	public List<Being> getAllFromPlayer(@PathVariable Long playerId) {

		return service.getAllFromPlayer(playerId);
	}

	@Override
	public List<Being> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		return service.getAllFromPlace(worldName, placeCode);
	}

	@Override
	public void destroyBeing(@PathVariable Long beingCode) {
		
		service.destroyBeing(beingCode);
	}
}
