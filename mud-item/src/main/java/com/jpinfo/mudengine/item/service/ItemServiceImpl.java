package com.jpinfo.mudengine.item.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.exception.IllegalParameterException;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;
import com.jpinfo.mudengine.item.model.MudItem;
import com.jpinfo.mudengine.item.model.MudItemAttr;
import com.jpinfo.mudengine.item.model.MudItemClass;
import com.jpinfo.mudengine.item.model.converter.ItemConverter;
import com.jpinfo.mudengine.item.model.converter.MudItemAttrConverter;
import com.jpinfo.mudengine.item.repository.ItemClassRepository;
import com.jpinfo.mudengine.item.repository.ItemRepository;
import com.jpinfo.mudengine.item.utils.ItemHelper;

@Service
public class ItemServiceImpl {
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private ItemClassRepository itemClassRepository;

	public Item getItem(Long itemId) {
		
		Item response = null;
		
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_NOT_FOUND));
		
		response = ItemConverter.convert(dbItem);
		
		return response;
	}
	
	public Item updateItem(Long itemId, Item requestItem) {
		
		Item response = null;
		
		// Retrieving the item record
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
			if (!dbItem.getItemClass().getCode().equals(requestItem.getItemClass().getCode())) {
				internalUpdateClass(dbItem, requestItem.getItemClass().getCode());
			}

			// Save the changes in database
			MudItem changedDbItem = itemRepository.save(dbItem);
			
			// Build the response
			response = ItemConverter.convert(changedDbItem);
		}
		
		
		return response;
	}
	
	private boolean internalSyncItemDuration(MudItem dbItem, Item requestItem) {
		
		boolean itemDestroyed = false;
		
		// Check current item duration
		// First, we obtain the max duration for this item
		// if this value is different from zero, it means that this is an item that can be destroyed
		Integer maxDuration = 
				dbItem.getAttrs().stream()
					.filter(d-> d.getCode().equals(ItemHelper.ITEM_MAX_DURATION_ATTR))
					.mapToInt(MudItemAttr::getValue)
					.findFirst()
					.orElse(0);
		
		// Retrieve the current duration of the item.  That value came from the request
		Integer currentDuration = requestItem.getAttrs().getOrDefault(ItemHelper.ITEM_DURATION_ATTR, 0);
		
		// If the currentItem has a duration and it is exhausted		
		itemDestroyed = (maxDuration!=0) && (currentDuration<=0);
		
		// Checks if the current duration is greater than maximum
		if ((maxDuration!=0) && (currentDuration > maxDuration)) {
			
			// Adjusts the current duration to the maximum
			dbItem.getAttrs().stream()
				.filter(d -> d.getCode().equals(ItemHelper.ITEM_DURATION_ATTR))
				.findFirst()
				.ifPresent(e -> e.setValue(maxDuration));
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
	
	public Item createItem(String itemClassCode, Optional<String> worldName, Optional<Integer> placeCode, 
			Optional<Integer> quantity, Optional<Long> owner) {
		
		Item response = null;
		
		boolean ownerPresent = owner.isPresent();
		boolean placePresent = (worldName.isPresent() && placeCode.isPresent());
		
		// Check if a minimum parameters are present
		if (!ownerPresent && !placePresent) {
			throw new IllegalParameterException(LocalizedMessages.ITEM_NO_OWNER);
		}
		
		// Check if there's only one set of
		if (ownerPresent && placePresent) {
			throw new IllegalParameterException(LocalizedMessages.ITEM_BOTH_OWNER);			
		}
		
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
			!requestItem.getAttrs().containsKey(d.getCode())			
		);

		// Looking for attributes to add/update
		for(String curAttr: requestItem.getAttrs().keySet()) {
			
			Integer curAttrValue = requestItem.getAttrs().get(curAttr);
			
			Optional<MudItemAttr> dbItemAttr =
				dbItem.getAttrs().stream()
					.filter(d -> d.getCode().equals(curAttr))
					.findFirst();
			
			if (dbItemAttr.isPresent()) {
				dbItemAttr.get().setValue(curAttrValue);
			} else {
				dbItem.getAttrs().add(
						MudItemAttrConverter.build(dbItem.getCode(), curAttr, curAttrValue)
						);
			}
		}
	}
	
	private MudItem internalSyncAttr(final MudItem dbItem, final MudItemClass previousItemClass, final MudItemClass itemClass) {
		
		// If a previous itemClass exist, remove the attributes set by it
		if (previousItemClass!=null) {

			dbItem.getAttrs().removeIf(d -> { 
				
				// Check if exists in the old class
				boolean existsInOldClass =
						previousItemClass.getAttrs().stream()
						.anyMatch(e -> e.getCode().equals(d.getCode()));
				
				// Check if exists in the new class
				boolean existsInNewClass =
						itemClass.getAttrs().stream()
						.anyMatch(e -> e.getCode().equals(d.getCode()));
				
				return existsInOldClass && (!existsInNewClass);
			});
		}
		
		// Mounting the list of attributes to add
		List<MudItemAttr> addAttrList = 
			itemClass.getAttrs().stream()
				// Filtering out those who already exists
				.filter(d ->  
						dbItem.getAttrs().stream()
								.noneMatch(e -> e.getCode().equals(d.getCode()))
					)
				// Converting List<MudItemClassAttr> to List<MudItemAttr>
				.map(d -> MudItemAttrConverter.build(dbItem.getCode(), d))
				.collect(Collectors.toList());

		// Adding those from new attr list
		dbItem.getAttrs().addAll(addAttrList);
		
		// Sync the duration attributes, if present
		dbItem.getAttrs().stream()
			.filter(d -> d.getCode().equals(ItemHelper.ITEM_MAX_DURATION_ATTR))
			.findFirst()
			.ifPresent(e -> 
			
				dbItem.getAttrs().stream()
					.filter(f -> f.getCode().equals(ItemHelper.ITEM_DURATION_ATTR))
					.findFirst()
					.ifPresent(g -> 
						g.setValue(e.getValue())
					)
		);

		return dbItem;
	}
	
	public Item destroyItem(Long itemId) {
		
		Item response;

		// Retrieving the database record
		MudItem dbItem = itemRepository
				.findById(itemId)
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.ITEM_NOT_FOUND));
		
		if (dbItem.getItemClass().getDemisedClassCode()!=null) {
			internalUpdateClass(dbItem, dbItem.getItemClass().getDemisedClassCode());
			
			response = ItemConverter.convert(dbItem);
			
		} else {
			
			itemRepository.delete(dbItem);
			
			response = ItemConverter.convert(null);
		}
		
		return response;
	}

	public List<Item> getAllFromPlace(String worldName, Integer placeCode) {
		
		List<Item> responseList;
		
		List<MudItem> dbResponse = itemRepository.findByCurWorldAndCurPlaceCode(worldName, placeCode);
		
		responseList = 
			dbResponse.stream()
				.map(ItemConverter::convert)
				.collect(Collectors.toList());
			
		
		return responseList;
	}

	public List<Item> getAllFromBeing(Long owner) {
		
		List<Item> responseList;
		
		List<MudItem> dbResponse = itemRepository.findByCurOwner(owner);

		responseList = 
				dbResponse.stream()
					.map(ItemConverter::convert)
					.collect(Collectors.toList());
		
		return responseList;
	}
}
