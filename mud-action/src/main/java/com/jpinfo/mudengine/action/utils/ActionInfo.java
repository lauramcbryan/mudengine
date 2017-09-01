package com.jpinfo.mudengine.action.utils;

import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class ActionInfo extends Action {
	
	private String actionCode;
	
	private Being actor;
	
	private Item mediator;
	
	private Place place;
	
	// {BEING, ITEM, PLACE, PLACE_CLASS}
	private ActionTarget target;
	
	private Reaction effect;
	
	public ActionInfo() {
		
	}
	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public Being getActor() {
		return actor;
	}

	public void setActor(Being actor) {
		this.actor = actor;
	}

	public Item getMediator() {
		return mediator;
	}

	public void setMediator(Item mediator) {
		this.mediator = mediator;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public ActionTarget getTarget() {
		return target;
	}

	public void setTarget(ActionTarget target) {
		this.target = target;
	}
	public Reaction getEffect() {
		return effect;
	}
	public void setEffect(Reaction effect) {
		this.effect = effect;
	}
	
	

}
