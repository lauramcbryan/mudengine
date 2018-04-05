package com.jpinfo.mudengine.common.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping("/place")
public interface PlaceService {

	/**
	 * Create a new place.  The new place will be placed next to an existing
	 * place according to the 'direction' parameter.
	 * 
	 * @param placeClassCode - class of the newly created place
	 * @param direction - direction leading to an existing place
	 * @param targetPlaceCode - existing place to link with the new one
	 */
	@RequestMapping(method=RequestMethod.PUT)
	ResponseEntity<Place> createPlace(@RequestParam("placeClassCode") String placeClassCode, @RequestParam("direction") String direction, @RequestParam("targetPlaceCode") Integer targetPlaceCode );
	
	/**
	 * Returns the place description and all related information about it.
	 * 
	 * The information returned by this call is the minimum required for showing a place and it's exits
	 * in a general manner.  The exits, by example, aren't fully expanded, remaining as a short message
	 * indicating the direction.  The client are encouraged to ask for further entity details. 
	 * 
	 * @param placeId - unique ID of the place being searched
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/{placeId}")
	Place getPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("placeId") Integer placeId);

	/**
	 * Update an existing place.
	 * 
	 * This operation can change these sections of a place:
	 * 
	 * - exits;
	 * - placeClass;
	 * 
	 * @param id
	 * @param requestPlace
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{placeId}")
	Place updatePlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("placeId") Integer placeId, @RequestBody Place requestPlace);
	
	/**
	 * Destroys a place.
	 * 
	 * This service is called in case an existing place is destroyed.
	 * In that case, the placeClass of the location is changed to another tagged as 'demise class' in the current place class.
	 * Size and weightCapacity remains the same, but all beings and items inside the place are lost. 
	 * 
	 * @param placeId
	 */
	@RequestMapping(method=RequestMethod.DELETE, value="/{placeId}")
	void destroyPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("placeId") Integer placeId);

}