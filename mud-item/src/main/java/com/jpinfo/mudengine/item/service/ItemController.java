package com.jpinfo.mudengine.item.service;

import java.util.*;

import java.util.stream.Collectors;

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
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.converter.ItemConverter;
import com.jpinfo.mudengine.item.model.converter.MudItemAttrConverter;
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
		
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_NOT_FOUND));
		
		response = ItemConverter.convert(dbItem);
		
		return response;
	}
	
	@Override
	public Item updateItem(@PathVariable Long itemId, @RequestBody Item requestItem) {
		
		Item response = null;
		
		// Retrieving the database record
		MudItem dbItem = itemRepository.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_NOT_FOUND));
		
		// 1. Updating basic fields
		dbItem.setCurWorld(requestItem.getCurWorld());
		dbItem.setCurPlaceCode(requestItem.getCurPlaceCode());
		dbItem.setQuantity(requestItem.getQuantity());
		dbItem.setCurOwner(requestItem.getCurOwner());
		
		// 2. Checking item attributes
		// ============================================
		internalSyncAttr(dbItem, requestItem);
		
		// 3. Check item duration
		// ============================================
		boolean itemToBeDestroyed = internalSyncItemDuration(dbItem, requestItem);
		
		if (itemToBeDestroyed) {
			
			// destroy the item
			destroyItem(itemId);
			
			// Retrieve it again from database
			dbItem = itemRepository.findById(itemId)
					.orElse(null);
			
			response = ItemConverter.convert(dbItem);
			
		} else {
			
			// 4. Check item class
			// ============================================
			
			// if itemClass has changed, resync the attributes of changed item
			if (!dbItem.getItemClass().getItemClassCode().equals(requestItem.getItemClassCode())) {
				internalUpdateClass(dbItem, requestItem.getItemClassCode());
			}

			// Save the changes in database
			MudItem changedDbItem = itemRepository.save(dbItem);
			
			// Build the response
			response = ItemConverter.convert(changedDbItem);
		}
		
		
		return response;
	}
	
	/**
	 * This method syncs up the attribute list present in database
	 * with the ones present in 
	 * @param dbItem
	 * @param requestItem
	 */
	private void internalSyncAttr(MudItem dbItem, Item requestItem) {

		// Looking for attributes to remove
		dbItem.getAttrs().removeIf(d -> 
			// Filtering attributes from db record that doesn't exist in request
			!requestItem.getAttrs().containsKey(d.getAttrCode())			
		);

		// Looking for attributes to add/update
		for(String curAttr: requestItem.getAttrs().keySet()) {
			
			Integer curAttrValue = requestItem.getAttrs().get(curAttr);
			
			Optional<MudItemAttr> dbItemAttr =
				dbItem.getAttrs().stream()
					.filter(d -> d.getAttrCode().equals(curAttr))
					.findFirst();
			
			if (dbItemAttr.isPresent()) {
				dbItemAttr.get().setAttrValue(curAttrValue);
			} else {
				dbItem.getAttrs().add(
						MudItemAttrConverter.build(dbItem.getItemCode(), curAttr, curAttrValue)
						);
			}
		}
	}
	
	private boolean internalSyncItemDuration(MudItem dbItem, Item requestItem) {
		
		boolean itemDestroyed = false;
		
		// Check current place health
		// First, we obtain the maxHP for this place
		// if this value is different from zero, it means that this is a place that can be destroyed
		Integer maxDuration = 
				dbItem.getAttrs().stream()
					.filter(d-> d.getAttrCode().equals(ItemHelper.ITEM_MAX_DURATION_ATTR))
					.mapToInt(MudItemAttr::getAttrValue)
					.findFirst()
					.orElse(0);
		
		// Retrieve the current HP of the place.  That value came from the request
		Integer currentDuration = requestItem.getAttrs().getOrDefault(ItemHelper.ITEM_DURATION_ATTR, 0);
		
		// If the currentItem has a duration and it is exhausted		
		itemDestroyed = (maxDuration!=0) && (currentDuration<=0);
		
		// Checks if the current duration is greater than maximum
		if ((maxDuration!=0) && (currentDuration > maxDuration)) {
			
			// Adjusts the current duration to the maximum
			dbItem.getAttrs().stream()
				.filter(d -> d.getAttrCode().equals(ItemHelper.ITEM_DURATION_ATTR))
				.findFirst()
				.ifPresent(e -> e.setAttrValue(maxDuration));
		}
		
		return itemDestroyed;
	}
	
	private void internalUpdateClass(MudItem dbItem, String itemClassCode) {

		MudItemClass dbClassItem = itemClassRepository
				.findById(itemClassCode)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_CLASS_NOT_FOUND));

		// Replace all current attributes from old class by the new one
		internalSyncAttr(dbItem, dbItem.getItemClass(), dbClassItem);
		dbItem.setItemClass(dbClassItem);
		
	}
	
	@Override
	public ResponseEntity<Item> createItem(@RequestParam String itemClassCode, @RequestParam Optional<String> worldName, @RequestParam Optional<Integer> placeCode, @RequestParam Optional<Integer> quantity, @RequestParam Optional<Long> owner) {
		
		Item response = null;
		
		// Check if a minimum parameters are present
		if (owner.isPresent() || (placeCode.isPresent() && worldName.isPresent())) {
		
			MudItemClass dbClassItem = itemClassRepository
					.findById(itemClassCode)
					.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_CLASS_NOT_FOUND));
			
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
				newDbItem.setQuantity(ItemHelper.CREATE_DEFAULT_QUANTITY);
		
			// Saving the entity (to get the itemCode)
			newDbItem = itemRepository.save(newDbItem);
		
			// Populating the attrs
			internalSyncAttr(newDbItem, null, dbClassItem);
		
			// Saving again
			itemRepository.save(newDbItem);
		
			// Building the response
			response = ItemConverter.convert(newDbItem);
			
		} else {
			throw new IllegalParameterException(LocalizedMessages.ITEM_NO_OWNER);
		}
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	private MudItem internalSyncAttr(final MudItem dbItem, final MudItemClass previousItemClass, final MudItemClass itemClass) {
		
		// If a previous itemClass exist, remove the attributes set by it
		if (previousItemClass!=null) {

			dbItem.getAttrs().removeIf(d -> { 
				
				// Check if exists in the old class
				boolean existsInOldClass =
						previousItemClass.getAttrs().stream()
						.anyMatch(e -> e.getAttrCode().equals(d.getAttrCode()));
				
				// Check if exists in the new class
				boolean existsInNewClass =
						itemClass.getAttrs().stream()
						.anyMatch(e -> e.getAttrCode().equals(d.getAttrCode()));
				
				return existsInOldClass && (!existsInNewClass);
			});
		}
		
		// Mounting the list of attributes to add
		List<MudItemAttr> addAttrList = 
			itemClass.getAttrs().stream()
				// Filtering out those who already exists
				.filter(d ->  
						dbItem.getAttrs().stream()
								.noneMatch(e -> e.getAttrCode().equals(d.getAttrCode()))
					)
				// Converting List<MudItemClassAttr> to List<MudItemAttr>
				.map(d -> MudItemAttrConverter.build(dbItem.getItemCode(), d))
				.collect(Collectors.toList());

		// Adding those from new attr list
		dbItem.getAttrs().addAll(addAttrList);

		return dbItem;
	}
	
	@Override
	public void destroyItem(@PathVariable Long itemId) {

		// Retrieving the database record
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_NOT_FOUND));
		
		if (dbItem.getItemClass().getDemiseItemClassCode()!=null) {
			internalUpdateClass(dbItem, dbItem.getItemClass().getDemiseItemClassCode());
		} else {
			
			itemRepository.delete(dbItem);
		}
	}

	@Override
	public List<Item> getAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<Item> responseList;
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		responseList = 
			dbResponse.stream()
				.map(ItemConverter::convert)
				.collect(Collectors.toList());
			
		
		return responseList;
	}

	@Override
	public List<Item> getAllFromBeing(@PathVariable Long owner) {
		
		List<Item> responseList;
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);

		responseList = 
				dbResponse.stream()
					.map(ItemConverter::convert)
					.collect(Collectors.toList());
		
		return responseList;
	}

	@Override
	public void destroyAllFromPlace(@PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		// We don't have any check for demisedItemClass here... it's assumed that the item is being destroyed
		// due to the place being destroyed.
		
		itemRepository.deleteAll(dbResponse);
	}

	@Override
	public void dropAllFromBeing(@PathVariable Long owner, @RequestParam String worldName, @RequestParam Integer placeCode) {
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);

		dbResponse.stream().forEach(d-> {
			
			d.setCurOwner(null);
			d.setCurWorld(worldName);
			d.setCurPlaceCode(placeCode);
			
			itemRepository.save(d);
		});
	}
	
}
