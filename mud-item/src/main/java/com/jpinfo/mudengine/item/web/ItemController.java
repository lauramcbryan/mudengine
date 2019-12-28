package com.jpinfo.mudengine.item.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<Item> createItem(@RequestParam String itemClassCode, @RequestParam Optional<String> worldName, @RequestParam Optional<Integer> placeCode, @RequestParam Optional<Integer> quantity, @RequestParam Optional<Long> owner) {
		
		return new ResponseEntity<>(
				service.createItem(itemClassCode, worldName, placeCode, quantity, owner), 
				HttpStatus.CREATED);
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
}
