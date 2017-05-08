package com.jpinfo.mudengine.item.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.interfaces.ItemService;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@RestController
public class ItemController implements ItemService {
	
	@Autowired
	ItemRepository repository;

	@Override
	public Item getItem(@PathVariable Integer id) {
		
		MudItem found = repository.findOne(id);
		
		Item response = ItemHelper.buildItem(found);
		
		return response;
	}
	
	@Override
	public void updateItem(@PathVariable Integer id, @RequestBody Item item) {
		
		// Retrieving the database record
		MudItem found = repository.findOne(id);
		
		// What can be updated
		found.setUsageCount(item.getUsageCount());
		
		repository.save(found);
	}
	
	@Override
	public Item insertItem(@RequestBody Item newItem) {
		
		MudItem newDbItem = new MudItem();
		newDbItem.setName(newItem.getName());
		newDbItem.setDescription(newItem.getDescription());
		newDbItem.setUsageCount(newItem.getUsageCount());
		newDbItem.setItemClass(newItem.getItemClass());
		
		// Saving the entity (to get the itemCode)
		newDbItem = repository.save(newDbItem);
		
		// Populating the attrs modifiers
		newDbItem = ItemHelper.updateItemAttrs(newDbItem, newItem);
		
		// Populating the skills modifiers
		newDbItem = ItemHelper.updateItemSkills(newDbItem, newItem);
		
		// Saving again
		repository.save(newDbItem);
		
		// Building the response
		Item response = ItemHelper.buildItem(newDbItem);
		
		return response;
	}
}
