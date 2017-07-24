package com.jpinfo.mudengine.common.service;

import java.util.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.item.Item;

@RequestMapping("/item")
public interface ItemService {

	@RequestMapping(method=RequestMethod.GET, value="{itemId}")
	Item getItem(@PathVariable("itemId") Integer itemId);

	@RequestMapping(method=RequestMethod.POST, value="{itemId}")
	Item updateItem(@PathVariable("itemId") Integer itemId, @RequestBody Item item);

	@RequestMapping(method=RequestMethod.PUT, value="/")
	Item createItem(@RequestParam("itemClassCode") String itemClassCode, @RequestParam("currentWorld") Optional<String> currentWorld, @RequestParam("currentPlace") Optional<Integer> currentPlace, @RequestParam("quantity") Integer quantity, @RequestParam("currentOwner") Optional<Long> currentOwner);
	
	@RequestMapping(method=RequestMethod.DELETE, value="{itemId}")
	Item destroyItem(@PathVariable("itemId") Integer itemId);

	@RequestMapping(method=RequestMethod.GET, value="/place/{worldName}/{placeCode}")
	List<Item> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);

	@RequestMapping(method=RequestMethod.DELETE, value="/place/{worldName}/{placeCode}")
	void deleteAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.GET, value="/being/{beingCode}")
	List<Item> getAllFromBeing(@PathVariable("beingCode") Long beingCode);
	
	@RequestMapping(method=RequestMethod.POST, value="/being/{beingCode}")
	void dropAllFromBeing(@PathVariable("beingCode") Long beingCode, @RequestParam("currentWorld") String currentWorld, @RequestParam("currentPlaceCode") Integer currentPlaceCode);	
}