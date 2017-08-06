package com.jpinfo.mudengine.item.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.service.ItemService;
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
	public Item getItem(@PathVariable Long itemId) {
		
		Item response = null;
		
		MudItem dbItem = itemRepository.findOne(itemId);
		
		if (dbItem!=null) {
			response = ItemHelper.buildItem(dbItem);
		} else {
			throw new EntityNotFoundException("Item entity not found"); 
		}
		
		return response;
	}
	
	@Override
	public Item updateItem(@PathVariable Long itemId, @RequestBody Item requestItem) {
		
		Item response = null;
		
		// Retrieving the database record
		MudItem dbItem = itemRepository.findOne(itemId);
		
		if (dbItem!=null) {
		
			dbItem.setCurWorld(requestItem.getCurWorld());
			dbItem.setCurPlaceCode(requestItem.getCurPlaceCode());
			dbItem.setQuantity(requestItem.getQuantity());
			dbItem.setCurOwner(requestItem.getCurOwner());
			
			// Looking for attributes to remove
			for(MudItemAttr curItemAttr: dbItem.getAttrs()) {
				
				// If it not exists in request, remove it
				if (requestItem.getAttrs().get(curItemAttr.getId().getAttrCode())==null) {
					dbItem.getAttrs().remove(curItemAttr);
				}
			}
			
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
			if (!dbItem.getItemClass().getItemClass().equals(requestItem.getItemClass())) {
			
				MudItemClass dbClassItem = itemClassRepository.findOne(requestItem.getItemClass());
				
				if (dbClassItem!=null) {
					dbItem = ItemHelper.changeItemAttrs(dbItem, dbItem.getItemClass(), dbClassItem);
					
					dbItem.setItemClass(dbClassItem);
					
				} else {
					throw new EntityNotFoundException("Item Class entity not found");
				}
			}
			
			dbItem = itemRepository.save(dbItem);
			
			response = ItemHelper.buildItem(dbItem);
			
		} else {
			throw new EntityNotFoundException("Item entity not found");
		}
		
		return response;
	}
	
	@Override
	public ResponseEntity<Item> createItem(@RequestParam String itemClassCode, @RequestParam Optional<String> worldName, @RequestParam Optional<Integer> placeCode, @RequestParam Optional<Integer> quantity, @RequestParam Optional<Long> owner) {
		
		Item response = null;
		
		// Check if a minimum parameters are present
		if (owner.isPresent() || (placeCode.isPresent() && worldName.isPresent())) {
		
			MudItemClass dbClassItem = itemClassRepository.findOne(itemClassCode);
			
			if (dbClassItem!=null) {
				
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
				throw new EntityNotFoundException("Item Class entity not found");
			}
		} else {
			throw new IllegalParameterException("At least owner or place must be set in request");
		}
		
		return new ResponseEntity<Item>(response, HttpStatus.CREATED);
	}
	
	public void destroyItem(@PathVariable Long itemId) {

		// Retrieving the database record
		MudItem dbItem = itemRepository.findOne(itemId);
		
		if (dbItem!=null) {
			
			itemRepository.delete(dbItem);
			
		} else {
			throw new EntityNotFoundException("Item entity not found"); 
		}
	}

	@Override
	public List<Item> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<Item> responseList = new ArrayList<Item>();
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		for(MudItem curdbItem: dbResponse) {
			
			responseList.add(ItemHelper.buildItem(curdbItem));
		}
		
		return responseList;
	}

	@Override
	public List<Item> getAllFromBeing(@PathVariable Long owner) {
		
		List<Item> responseList = new ArrayList<Item>();
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);
		
		for(MudItem curdbItem: dbResponse) {
			
			responseList.add(ItemHelper.buildItem(curdbItem));
		}
		
		return responseList;
	}

	@Override
	public void destroyAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		for(MudItem curdbItem: dbResponse) {
			
			itemRepository.delete(curdbItem);
		}
		
	}

	@Override
	public void dropAllFromBeing(@PathVariable Long owner, @RequestParam String worldName, @RequestParam Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);
		
		for(MudItem curdbItem: dbResponse) {
			
			curdbItem.setCurOwner(null);
			curdbItem.setCurWorld(worldName);
			curdbItem.setCurPlaceCode(placeCode);
			
			itemRepository.save(curdbItem);
		}
		
	}
	
}
