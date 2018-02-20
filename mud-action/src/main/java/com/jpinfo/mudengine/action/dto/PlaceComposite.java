package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class PlaceComposite implements ActionTarget {
	
	private Place place;
	
	private List<Item> itens;
	
	private List<Being> beings;
	
	private List<ActionMessage> messages;

	public PlaceComposite(Place simplePlace) {
		
		this.place = simplePlace;
		this.messages = new ArrayList<ActionMessage>();
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
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
	public void describeIt(ActionTarget target) {
		
		target.addMessage(null, "{str:THISPLACEIS}", this.getPlace().getPlaceClass().getName());
		
		// TODO Include exits, items, beings
		
	}
}
