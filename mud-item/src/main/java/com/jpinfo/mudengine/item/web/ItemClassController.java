package com.jpinfo.mudengine.item.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.itemclass.ItemClass;
import com.jpinfo.mudengine.common.service.ItemClassService;
import com.jpinfo.mudengine.item.service.ItemClassServiceImpl;

@RestController
public class ItemClassController implements ItemClassService {
	
	@Autowired
	private ItemClassServiceImpl service;
	
	@Override
	public ItemClass getItemClass(@PathVariable String id) {
		
		return service.getItemClass(id);
	}

}
