package com.jpinfo.mudengine.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.item.model.ItemClass;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;

@RestController
@RequestMapping("/class")
public class ItemClassController {
	
	@Autowired
	private ItemClassRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public ItemClass getItemClass(@PathVariable String id) {
		
		ItemClass found = repository.findOne(id);
		
		return found;
	}

}
