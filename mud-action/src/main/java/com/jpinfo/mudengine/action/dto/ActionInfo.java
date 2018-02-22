package com.jpinfo.mudengine.action.dto;


import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.item.Item;

public class ActionInfo extends Action {
	
	private ActionClass actionClass;
	
	private BeingComposite actor;
	
	private Item mediator;
	
	// {BEING, ITEM, PLACE}
	private ActionTarget target;
	
	private Double successRate;
	
	
	public ActionInfo() {
	}
	
	public BeingComposite getActor() {
		return actor;
	}

	public void setActor(BeingComposite actor) {
		this.actor = actor;
	}

	public Item getMediator() {
		return mediator;
	}

	public void setMediator(Item mediator) {
		this.mediator = mediator;
	}

	public ActionTarget getTarget() {
		return target;
	}

	public void setTarget(ActionTarget target) {
		this.target = target;
	}

	public ActionClass getActionClass() {
		return actionClass;
	}

	public void setActionClass(ActionClass actionClass) {
		this.actionClass = actionClass;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}
	
}
