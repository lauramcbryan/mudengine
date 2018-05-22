package com.jpinfo.mudengine.item.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.service.ItemService;
import com.jpinfo.mudengine.common.utils.CommonConstants;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@RestController
public class ItemController implements ItemService {
	
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	ItemClassRepository itemClassRepository;

	@Override
	public Item getItem(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long itemId) {
		
		Item response = null;
		
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException("Item entity not found"));
		
		response = ItemHelper.buildItem(dbItem);
		
		return response;
	}
	
	@Override
	public Item updateItem(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long itemId, @RequestBody Item requestItem) {
		
		Item response = null;
		
		// Retrieving the database record
		MudItem dbItem = itemRepository.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException("Item entity not found"));
		
		dbItem.setCurWorld(requestItem.getCurWorld());
		dbItem.setCurPlaceCode(requestItem.getCurPlaceCode());
		dbItem.setQuantity(requestItem.getQuantity());
		dbItem.setCurOwner(requestItem.getCurOwner());
		
		// Adjusting the attributes in database against those in request
		// (This is done even if the itemClass has changed the attributes)
			
		// Looking for attributes to remove
		List<MudItemAttr> removeList = 
			dbItem.getAttrs().stream()
				// Filtering attributes from db record that doesn't exist in request
				.filter(d -> !requestItem.getAttrs().containsKey(d.getId().getAttrCode()))
				.collect(Collectors.toList());

		// Remove from list all records found
		dbItem.getAttrs().removeAll(removeList);

		// Looking for attributes to add
		for(String curAttr: requestItem.getAttrs().keySet()) {
			
			boolean found = false;
			for(MudItemAttr curItemAttr: dbItem.getAttrs()) {
				
				if (curItemAttr.getId().getAttrCode().equals(curAttr)) {
					curItemAttr.setAttrValue(requestItem.getAttrs().get(curAttr));
					found = true;
				}
			}
			
			if (!found) {
				dbItem.getAttrs().add(
						ItemHelper.buildMudItemAttr(dbItem.getItemCode(), curAttr, requestItem.getAttrs().get(curAttr))
						);
			}
		}

		// if the itemClass is changing, reset the attributes
		if (!dbItem.getItemClass().getItemClass().equals(requestItem.getItemClassCode())) {
		
			MudItemClass dbClassItem = itemClassRepository
					.findById(requestItem.getItemClassCode())
					.orElseThrow(() -> new EntityNotFoundException("Item Class entity not found"));

			// Replace all current attributes from old class by the new one
			dbItem = ItemHelper.changeItemAttrs(dbItem, dbItem.getItemClass(), dbClassItem);
			dbItem.setItemClass(dbClassItem);
		} 
		
		
		dbItem = itemRepository.save(dbItem);
		
		response = ItemHelper.buildItem(dbItem);
		
		return response;
	}
	
	@Override
	public ResponseEntity<Item> createItem(@RequestParam String itemClassCode, @RequestParam Optional<String> worldName, @RequestParam Optional<Integer> placeCode, @RequestParam Optional<Integer> quantity, @RequestParam Optional<Long> owner) {
		
		Item response = null;
		
		// Check if a minimum parameters are present
		if (owner.isPresent() || (placeCode.isPresent() && worldName.isPresent())) {
		
			MudItemClass dbClassItem = itemClassRepository
					.findById(itemClassCode)
					.orElseThrow(() -> new EntityNotFoundException("Item Class entity not found"));
			
			MudItem newDbItem = new MudItem();
			newDbItem.setItemClass(dbClassItem);
			
			if (worldName.isPresent())
				newDbItem.setCurWorld(worldName.get());
			
			if (placeCode.isPresent())
				newDbItem.setCurPlaceCode(placeCode.get());
			
			if (owner.isPresent())
				newDbItem.setCurOwner(owner.get());

			if (quantity.isPresent())
				newDbItem.setQuantity(quantity.get());
			else
				newDbItem.setQuantity(1);
		
			// Saving the entity (to get the itemCode)
			newDbItem = itemRepository.save(newDbItem);
		
			// Populating the attrs
			newDbItem = ItemHelper.changeItemAttrs(newDbItem, null, dbClassItem);
		
			// Saving again
			itemRepository.save(newDbItem);
		
			// Building the response
			response = ItemHelper.buildItem(newDbItem);
			
		} else {
			throw new IllegalParameterException("At least owner or place must be set in request");
		}
		
		return new ResponseEntity<Item>(response, HttpStatus.CREATED);
	}
	
	@Override
	public void destroyItem(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long itemId) {

		// Retrieving the database record
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException("Item entity not found"));
		
		itemRepository.delete(dbItem);
	}

	@Override
	public List<Item> getAllFromPlace(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<Item> responseList = new ArrayList<Item>();
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		
		dbResponse.stream().forEach(d-> {
			responseList.add(ItemHelper.buildItem(d));
		});
		
		return responseList;
	}

	@Override
	public List<Item> getAllFromBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long owner) {
		
		List<Item> responseList = new ArrayList<Item>();
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);
		
		dbResponse.stream().forEach(d-> {
			responseList.add(ItemHelper.buildItem(d));
		});
		
		return responseList;
	}

	@Override
	public void destroyAllFromPlace(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		dbResponse.stream().forEach(d-> {
			itemRepository.delete(d);
		});
	}

	@Override
	public void dropAllFromBeing(@RequestHeader(CommonConstants.AUTH_TOKEN_HEADER) String authToken, @PathVariable Long owner, @RequestParam String worldName, @RequestParam Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);

		dbResponse.stream().forEach(d-> {
			
			d.setCurOwner(null);
			d.setCurWorld(worldName);
			d.setCurPlaceCode(placeCode);
			
			itemRepository.save(d);
		});
	}
	
}
