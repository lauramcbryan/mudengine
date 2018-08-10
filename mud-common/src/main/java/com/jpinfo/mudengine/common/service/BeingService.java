package com.jpinfo.mudengine.common.service;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.being.Being;

@RequestMapping("/being")
public interface BeingService {

	@RequestMapping(method=RequestMethod.GET, value="{beingCode}")
	Being getBeing(@PathVariable("beingCode") Long beingCode);

	@RequestMapping(method=RequestMethod.POST, value="/{beingCode}")
	Being updateBeing(@PathVariable("beingCode") Long beingCode, @RequestBody Being requestBeing);

	@RequestMapping(method=RequestMethod.PUT)
	ResponseEntity<Being> createBeing( 
			@RequestParam("beingType") Being.enumBeingType beingType, @RequestParam("beingClass") String beingClass, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode, 
			@RequestParam("quantity") Integer quantity, @RequestParam("beingName") String beingName);

	@RequestMapping(method=RequestMethod.PUT, value="/player/{playerId}")
	ResponseEntity<Being> createPlayerBeing(
			@PathVariable("playerId") Long playerId, @RequestParam("beingClass") String beingClass, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode, 
			@RequestParam("beingName") String beingName);
	
	@RequestMapping(method=RequestMethod.GET,  value="/player/{playerId}")
	List<Being> getAllFromPlayer(@PathVariable("playerId") Long playerId);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{beingCode}")
	void destroyBeing(@PathVariable("beingCode") Long beingCode);

	@RequestMapping(method=RequestMethod.GET,  value="/place/{worldName}/{placeCode}")
	List<Being> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/place/{worldName}/{placeCode}")
	void destroyAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/player/{playerId}")
	void destroyAllFromPlayer(@PathVariable("playerId") Long playerId);
	
}