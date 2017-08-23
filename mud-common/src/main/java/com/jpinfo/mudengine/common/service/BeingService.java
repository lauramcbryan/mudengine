package com.jpinfo.mudengine.common.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping("/being")
public interface BeingService {

	@RequestMapping(method=RequestMethod.GET, value="{beingCode}")
	Being getBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("beingCode") Long beingCode);

	@RequestMapping(method=RequestMethod.POST, value="/{beingCode}")
	Being updateBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("beingCode") Long beingCode, @RequestBody Being requestBeing);

	@RequestMapping(method=RequestMethod.PUT)
	ResponseEntity<Being> createBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@RequestParam("beingType") Integer beingType, @RequestParam("beingClass") String beingClass, 
			@RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode, 
			@RequestParam("quantity") Optional<Integer> quantity, @RequestParam("playerId") Optional<Long> playerId);

	@RequestMapping(method=RequestMethod.GET,  value="/player/{playerId}")
	List<Being> getAllFromPlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("playerId") Long playerId);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{beingCode}")
	void destroyBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("beingCode") Long beingCode);

	@RequestMapping(method=RequestMethod.GET,  value="/place/{worldName}/{placeCode}")
	List<Being> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/place/{worldName}/{placeCode}")
	void destroyAllFromPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/player/{playerId}")
	void destroyAllFromPlayer(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("playerId") Long playerId);
}