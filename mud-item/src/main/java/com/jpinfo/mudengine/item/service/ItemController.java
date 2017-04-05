package com.jpinfo.mudengine.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.item.model.Item;
import com.jpinfo.mudengine.item.repository.ItemRepository;

@RestController
@RequestMapping("/item")
public class ItemController {
	
	@Autowired
	ItemRepository repository;

	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public Item getItem(@PathVariable Integer id) {
		
		Item found = repository.findOne(id);
		
		return found;
		
	}
}
