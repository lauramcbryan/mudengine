package com.jpinfo.mudengine.common.service;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping("/item")
public interface ItemService {

	@RequestMapping(method=RequestMethod.GET, value="{itemId}")
	Item getItem(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("itemId") Long itemId);

	@RequestMapping(method=RequestMethod.POST, value="{itemId}")
	Item updateItem(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("itemId") Long itemId, @RequestBody Item item);

	@RequestMapping(method=RequestMethod.PUT, value="/")
	ResponseEntity<Item> createItem(@RequestParam("itemClassCode") String itemClassCode, @RequestParam("worldName") Optional<String> worldName, @RequestParam("placeCode") Optional<Integer> placeCode, @RequestParam("quantity") Optional<Integer> quantity, @RequestParam("owner") Optional<Long> owner);
	
	@RequestMapping(method=RequestMethod.DELETE, value="{itemId}")
	void destroyItem(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("itemId") Long itemId);

	@RequestMapping(method=RequestMethod.GET, value="/place/{worldName}/{placeCode}")
	List<Item> getAllFromPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);

	@RequestMapping(method=RequestMethod.DELETE, value="/place/{worldName}/{placeCode}")
	void destroyAllFromPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);
	
	@RequestMapping(method=RequestMethod.GET, value="/being/{owner}")
	List<Item> getAllFromBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("owner") Long owner);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/being/{owner}")
	void dropAllFromBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("owner") Long owner, @RequestParam("worldName") String worldName, @RequestParam("placeCode") Integer placeCode);	
}