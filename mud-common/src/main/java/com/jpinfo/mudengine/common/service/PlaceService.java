package com.jpinfo.mudengine.common.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExits;

@RequestMapping("/place")
public interface PlaceService {

	/**
	 * Returns the place description and all related information about it.
	 * 
	 * The information returned by this call is the minimum required for showing a place and it´s exits
	 * in a general manner.  The exits, by example, aren't fully expanded, remaining as a short message
	 * indicating the direction.  The client are encouraged to ask for further entity details. 
	 * 
	 * @param id - unique ID of the place being searched
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	Place getPlace(@PathVariable("id") Integer id);

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
	Place updatePlace(@PathVariable("placeId") Integer placeId, @RequestBody Place requestPlace);
	
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
	Place destroyPlace(@PathVariable("placeId") Integer placeId);

	/**
	 * Create an exit on selected place.
	 * 
	 * The referenced place target of this exit will be updated as well with a new exit in direction
	 * diametrically opposite to this exit. Exit name is not required for this operation and will be ignored.
	 * (Name will be pulled from placeClass for any involved entities.)
	 * 
	 * If referenced place doesn´t exist an error will be thrown.
	 * 
	 * @param placeId - ID of the place which exits will be updated
	 * @param direction - one of the directions allowed for the map
	 * @param newExit - new exit data
	 */
	@RequestMapping(method=RequestMethod.PUT, value="/{placeId}/exit/{direction}")
	Place createExit(@PathVariable("placeId") Integer placeId, @PathVariable("direction") String direction, @RequestBody PlaceExits newExit);
	
	/**
	 * Update an existing exit.
	 * 
	 * If referenced place changes, an update will be performed as well to remove the exit from old place
	 * and add it in the new referenced place.
	 * 
	 * @param placeId
	 * @param direction
	 * @param newExit
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{placeId}/exit/{direction}")
	Place updateExit(@PathVariable("placeId") Integer placeId, @PathVariable("direction") String direction, @RequestBody PlaceExits newExit);
	
	/**
	 * Destroy an exit.
	 * The referenced place will have its exit updated as well.
	 * 
	 * @param placeId
	 * @param direction
	 */
	@RequestMapping(method=RequestMethod.DELETE, value="/{placeId}/exit/{direction}")
	Place destroyExit(@PathVariable("placeId") Integer placeId, @PathVariable("direction") String direction);
	
	/**
	 * Update the items in a location.
	 * 
	 * This call performs a straightforward update in specified location increasing (or including if it doesn't
	 * exist) the selected quantity of items
	 * 
	 * The qtty parameter accepts relative values.  The resulting item quantity in the place will be computed by
	 * Place.Item[itemCode].qtty+=service.qtty
	 * 
	 * If this isn't the expected behavior the service can be called with qttyMode parameter as ABSOLUTE.  In that way,
	 * the resulting qtty of the item will be the one provided in service´s request.
	 * 
	 * The sizeLeft and capacityLeft of the place will be updated according to new item qtty.
	 * If the added items size or weight exceeds the location capacity an error will be throw.
	 * 
	 * @param placeId - code of the place
	 * @param itemCode - code of the item
	 * @param qtty - new qtty of the item in the place
	 * @param qttyMode - either ABSOLUTE or RELATIVE
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{placeId}/item/{itemCode}")
	Place updateItems(@PathVariable("placeId") Integer placeId, @PathVariable("itemCode") Integer itemCode, @RequestParam(name="qtty", defaultValue="1") Integer qtty, @RequestParam(name="qttyMode", defaultValue="RELATIVE") String qttyMode);
	
	/**
	 * Remove specified item from a location.
	 * 
	 * This call is the same as calling POST verb with zero qtty for specified item. 
	 * 
	 * @param placeId
	 * @param itemCode
	 */
	@RequestMapping(method=RequestMethod.DELETE, value="/{placeId}/item/{itemCode}")
	Place removeItems(@PathVariable("placeId") Integer placeId, @PathVariable("itemCode") Integer itemCode);
	

	/**
	 * Updates the quantity of specified beings in the location.
	 * 
	 * If the being is a TEAM class, it will have only its quantity updated.
	 * If isn't a TEAM the original location of the being will be searched (only in current world) and removed from there.
	 * 
	 * The qtty parameter accepts relative values.  The resulting being quantity in the place will be computed by
	 * Place.Beings[beingCode].qtty+=service.qtty
	 * 
	 * If this isn´t the expected behavior the service can be called with qttyMode parameter as ABSOLUTE.  In that way,
	 * the resulting qtty of the item will be the one provided in service´s request.
	 * 
	 * The sizeLeft and capacityLeft of the place will be updated according to new being qtty.
	 * If the added beings size or weight exceeds the location capacity an error will be throw.
	 * 
	 * @param placeId
	 * @param beingId
	 * @param qtty
	 * @param qttyMode
	 */
	@RequestMapping(method=RequestMethod.POST, value="/{placeId}/being/{beingId}")
	Place updateBeings(@PathVariable("placeId") Integer placeId, @PathVariable("beingId") Long beingId, @RequestParam(name="qtty", defaultValue="1") Integer qtty, @RequestParam(name="qttyMode", defaultValue="RELATIVE") String qttyMode);
	
	/**
	 * Remove specified being from a location.
	 * 
	 * This call is the same as calling POST verb with zero qtty for specified being.
	 * 
	 * @param placeId
	 * @param beingId
	 */
	@RequestMapping(method=RequestMethod.DELETE, value="/{placeId}/being/{beingId}")
	Place removeBeings(@PathVariable("placeId") Integer placeId, @PathVariable("beingId") Long beingId);
	

}