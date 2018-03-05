package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;
import com.jpinfo.mudengine.common.place.PlaceExit;

public class PlaceComposite implements ActionTarget {
	
	private Place place;
	
	private List<Item> items;
	
	private List<Being> beings;
	
	private List<ActionMessage> messages;

	public PlaceComposite(Place simplePlace) {
		
		this.place = simplePlace;
		this.messages = new ArrayList<ActionMessage>();
		this.beings = new ArrayList<Being>();
		this.items = new ArrayList<Item>();
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Being> getBeings() {
		return beings;
	}

	public void setBeings(List<Being> beings) {
		this.beings = beings;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public List<ActionMessage> getMessages() {
		
		return this.messages;
	}

	@Override
	public void addMessage(Long senderCode, String messageKey, String... args) {
		
		this.messages.add(new ActionMessage(senderCode, this.place.getPlaceCode().longValue(), 
				EnumTargetType.PLACE, messageKey, args));
	}
	
	@Override
	public void addMessage(String messageKey, String... args) {
		this.addMessage(null,  messageKey, args);
	}


	@Override
	public void describeIt(ActionTarget target) {
		
		target.addMessage("{str:THISPLACEIS}", this.getPlace().getPlaceClass().getName());
		target.addMessage("{str:THISPLACEDESC}", this.getPlace().getPlaceClass().getDescription());


		// =========== EXITS ==========
		target.addMessage("{str:EXITHEADER");
		
		// Traversing for all exits
		for (String curDirection: this.getPlace().getExits().keySet()) {
			
			PlaceExit curExit = this.getPlace().getExits().get(curDirection);
			
			if (curExit.isVisible()) {
				target.addMessage("{str:EXIT}", curDirection, curExit.getName());
			}
		}
		if (this.getPlace().getExits().isEmpty())
			target.addMessage("{str:NOEXIT");


		// =========== BEINGS ==========
		target.addMessage("{str:BEINGHEADER");
		
		for(Being curBeing: this.getBeings()) {
			
			if (curBeing.getBeingType().equals(Being.BEING_TYPE_REGULAR_NON_SENTIENT)) {
				target.addMessage("{str:PACKOFBEINGS}", curBeing.getBeingClass().getName());
			} else if (curBeing.getBeingType().equals(Being.BEING_TYPE_REGULAR_SENTIENT)) {
				target.addMessage("{str:GROUPOFBEINGS}", curBeing.getBeingClass().getName());
			} else {
				target.addMessage("{str:HEREIS}", curBeing.getName());
			}
		}

		if (this.getBeings().isEmpty()) {
			target.addMessage("{str:NOBODY");			
		}
		
		
		// =========== ITEMS ==========
		target.addMessage("{str:ITEMHEADER");

		for(Item curItem: this.getItems()) {
			target.addMessage("{str:SIMPLESTR}", curItem.getItemClass().getDescription());
		}
		
		if (this.getItems().isEmpty()) {
			target.addMessage("{str:NOTHING");
		}
	}
}
