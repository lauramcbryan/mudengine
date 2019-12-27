package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.being.Being;

@RequestMapping("/being")
public interface BeingService {

	@GetMapping(value="{beingCode}")
	Being getBeing(@PathVariable("beingCode") Long beingCode);

	@PostMapping(value="/{beingCode}")
	Being updateBeing(@PathVariable("beingCode") Long beingCode, @RequestBody Being requestBeing);
	
	@PutMapping
	ResponseEntity<Being> createBeing( 
			@RequestParam("beingType") Being.enumBeingType beingType, @RequestParam("beingClass") String beingClass, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode, 
			@RequestParam("quantity") Integer quantity, @RequestParam("beingName") String beingName);

	@DeleteMapping(value="/{beingCode}")
	void destroyBeing(@PathVariable("beingCode") Long beingCode);
	
	@PutMapping(value="/player/{playerId}")
	ResponseEntity<Being> createPlayerBeing(
			@PathVariable("playerId") Long playerId, @RequestParam("beingClass") String beingClass, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode, 
			@RequestParam("beingName") String beingName);
	
	@GetMapping(value="/player/{playerId}")
	List<Being> getAllFromPlayer(@PathVariable("playerId") Long playerId);

	@GetMapping(value="/place/{worldName}/{placeCode}")
	List<Being> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
}