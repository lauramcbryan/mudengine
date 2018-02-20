package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class BeingComposite implements ActionTarget  {

	private Being being;
	
	private List<Item> inventory;
	
	private Place place;
	
	private List<ActionMessage> messages;
	
	public BeingComposite() {
		this.messages = new ArrayList<ActionMessage>();		
	}
	
	public BeingComposite(Being simpleBeing) {
		this.being = simpleBeing;
		
		this.messages = new ArrayList<ActionMessage>();
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

	public List<ActionMessage> getMessages() {
		return messages;
	}
	
	public void addMessage(Long senderCode, String messageKey, String... parms) {
		
		this.messages.add(new ActionMessage(senderCode, this.getBeing().getBeingCode(), 
				EnumTargetType.BEING, messageKey, parms));
	}

	@Override
	public void describeIt(ActionTarget target) {

		// TODO Add more information about the being
		if (getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_NON_SENTIENT)) {
			target.addMessage(null, "{str:PACKOFBEINGS}", getBeing().getBeingClass().getName());
		} else if (getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_SENTIENT)) {
			target.addMessage(null, "{str:GROUPOFBEINGS}", getBeing().getBeingClass().getName());
		} else {
			target.addMessage(null, "{str:HEREIS}", getBeing().getName());
		}
		

	}
	
	public void describeYourself() {
		
		// TODO Add more information about yourself
		this.addMessage(null, "{str:YOUAREIN}", getPlace().getPlaceClass().getName());	
		this.addMessage(null, "{str:YOUAREINDESC}", getPlace().getPlaceClass().getDescription());
	}
	
}
