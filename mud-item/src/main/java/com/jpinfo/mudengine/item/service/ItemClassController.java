package com.jpinfo.mudengine.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;

@RestController
@RequestMapping("/item/class")
public class ItemClassController {
	
	@Autowired
	private ItemClassRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="{id}")
	public MudItemClass getItemClass(@PathVariable String id) {
		
		MudItemClass found = repository.findOne(id);
		
		return found;
	}

}
