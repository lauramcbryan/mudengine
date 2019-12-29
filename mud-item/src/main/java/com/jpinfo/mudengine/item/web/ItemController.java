package com.jpinfo.mudengine.item.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.service.ItemService;
import com.jpinfo.mudengine.item.service.ItemServiceImpl;

@RestController
public class ItemController implements ItemService {
	
	@Autowired
	private ItemServiceImpl service;
	

	@Override
	public Item getItem(@PathVariable Long itemId) {
		
		return service.getItem(itemId);
	}
	
	@Override
	public Item updateItem(@PathVariable Long itemId, @RequestBody Item requestItem) {
		
		return service.updateItem(itemId, requestItem);
	}
	
	@Override
	public Item destroyItem(@PathVariable Long itemId) {
		
		return service.destroyItem(itemId);
	}

	@Override
	public List<Item> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		return service.getAllFromPlace(worldName, placeCode);
	}

	@Override
	public List<Item> getAllFromBeing(@PathVariable Long owner) {
		
		return service.getAllFromBeing(owner);
	}

	@Override
	public ResponseEntity<Item> createOwnedItem(Long owner, 
			String itemClassCode, Integer quantity) {
		
		return new ResponseEntity<>(
				service.createItem(itemClassCode, null, null, quantity, owner), 
				HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Item> createNoOwnerItem(String worldName, Integer placeCode, 
			String itemClassCode, Integer quantity) {
		
		return new ResponseEntity<>(
				service.createItem(itemClassCode, worldName, placeCode, quantity, null), 
				HttpStatus.CREATED);
	}
}
