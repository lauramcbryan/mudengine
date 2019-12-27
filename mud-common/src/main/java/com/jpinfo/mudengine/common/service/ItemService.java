package com.jpinfo.mudengine.common.service;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.item.Item;

@RequestMapping("/item")
public interface ItemService {

	@GetMapping(value="{itemId}")
	Item getItem(@PathVariable("itemId") Long itemId);

	@PostMapping(value="{itemId}")
	Item updateItem(@PathVariable("itemId") Long itemId, @RequestBody Item item);

	@PutMapping()
	ResponseEntity<Item> createItem(@RequestParam("itemClassCode") String itemClassCode, @RequestParam("worldName") Optional<String> worldName, @RequestParam("placeCode") Optional<Integer> placeCode, @RequestParam("quantity") Optional<Integer> quantity, @RequestParam("owner") Optional<Long> owner);
	
	@DeleteMapping(value="{itemId}")
	Item destroyItem(@PathVariable("itemId") Long itemId);

	@GetMapping(value="/place/{worldName}/{placeCode}")
	List<Item> getAllFromPlace(@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);

	@GetMapping(value="/being/{owner}")
	List<Item> getAllFromBeing(@PathVariable("owner") Long owner);
}