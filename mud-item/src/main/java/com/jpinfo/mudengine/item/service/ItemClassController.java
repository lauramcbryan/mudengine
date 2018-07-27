package com.jpinfo.mudengine.item.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.itemclass.ItemClass;
import com.jpinfo.mudengine.common.service.ItemClassService;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@RestController
public class ItemClassController implements ItemClassService {
	
	@Autowired
	private ItemClassRepository repository;
	
	@Override
	public ItemClass getItemClass(@PathVariable String id) {
		
		MudItemClass found = repository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Item Class entity not found"));
		
		return ItemHelper.buildItemClass(found);
	}

}
