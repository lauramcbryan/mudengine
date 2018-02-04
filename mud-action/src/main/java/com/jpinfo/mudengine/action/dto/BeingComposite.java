package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jpinfo.mudengine.action.utils.ActionMessages;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class BeingComposite implements ActionTarget  {

	private Being being;
	
	private List<Item> inventory;
	
	private Place place;
	
	private List<ActionMessages> messages;
	
	public BeingComposite() {
		
	}
	
	public BeingComposite(Being simpleBeing) {
		this.being = simpleBeing;
		
		this.messages = new ArrayList<ActionMessages>();
	}

	public List<Item> getInventory() {
		return inventory;
	}

	public void setInventory(List<Item> inventory) {
		this.inventory = inventory;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public Being getBeing() {
		return being;
	}

	@Override
	public Collection<Reaction> getReactions(String actionCode, boolean isBefore) {
		return this.being.getReactions(actionCode, isBefore);
	}

	public List<ActionMessages> getMessages() {
		return messages;
	}
	
	public void addMessage(String messageKey, Object... parms) {
		
		this.messages.add(new ActionMessages(this.getBeing().getBeingCode(), messageKey, parms));
		
	}
	
}
