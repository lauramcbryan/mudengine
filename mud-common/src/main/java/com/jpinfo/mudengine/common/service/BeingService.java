package com.jpinfo.mudengine.common.service;

import java.util.List;
import java.util.Optional;

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
	Being createBeing(
			@RequestParam("beingType") Integer beingType, @RequestParam("beingClass") String beingClass, 
			@RequestParam("currentWorld") String currentWorld, @RequestParam("currentPlace") Integer currentPlace, 
			@RequestParam("quantity") Optional<Integer> quantity);

	@RequestMapping(method=RequestMethod.GET,  value="/player/{playerId}")
	List<Being> getAllFromPlayer(@PathVariable("playerId") Long playerId);
	
	@RequestMapping(method=RequestMethod.GET,  value="/place/{worldName}/{placeCode}")
	List<Being> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);

	@RequestMapping(method=RequestMethod.DELETE, value="/{beingCode}")
	Being destroyBeing(@PathVariable("beingCode") Long beingCode);
}