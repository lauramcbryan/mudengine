package com.jpinfo.mudengine.common.service;

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
	Item createItem(@RequestParam("itemClassCode") String itemClassCode, @RequestParam("currentWorld") String currentWorld, @RequestParam("currentPlace") Integer currentPlace, @RequestParam("quantity") Integer quantity);
	
	@RequestMapping(method=RequestMethod.DELETE, value="{itemId}")
	Item destroyItem(@PathVariable("itemId") Integer itemId);

}