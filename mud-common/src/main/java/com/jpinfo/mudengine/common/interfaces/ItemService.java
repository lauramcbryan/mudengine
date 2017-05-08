package com.jpinfo.mudengine.common.interfaces;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jpinfo.mudengine.common.item.Item;

@RequestMapping("/item")
public interface ItemService {

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	Item getItem(@PathVariable("id") Integer id);

	@RequestMapping(method=RequestMethod.POST, value="{id}")
	void updateItem(@PathVariable("id") Integer id, @RequestBody Item item);

	@RequestMapping(method=RequestMethod.PUT)
	Item insertItem(@RequestBody Item newItem);

}