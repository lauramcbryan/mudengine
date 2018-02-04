package com.jpinfo.mudengine.action.dto;

import java.util.Collection;
import java.util.List;

import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class PlaceComposite implements ActionTarget {
	
	private Place place;
	
	private List<Item> itens;
	
	private List<Being> beings;

	public PlaceComposite(Place simplePlace) {
		
		this.place = simplePlace;
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
	public Collection<Reaction> getReactions(String actionCode, boolean isBefore) {
		
		return this.place.getReactions(actionCode, isBefore);
	}
	
	

}
