package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.utils.ActionMessages;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.interfaces.ActionTarget;
import com.jpinfo.mudengine.common.interfaces.Reaction;
import com.jpinfo.mudengine.common.item.Item;

public class ActionInfo extends Action {
	
	private String actionCode;
	
	private BeingComposite actor;
	
	private Item mediator;
	
	// {BEING, ITEM, PLACE}
	private ActionTarget target;
	
	private Reaction effect;
	
	private List<ActionMessages> broadcastMessages;
	
	public ActionInfo() {
		this.broadcastMessages = new ArrayList<ActionMessages>();
		
	}

	public void sendMessageTo(Long targetCode, EnumTargetType targetType, String messageKey, Object... args) {
		this.broadcastMessages.add(new ActionMessages(targetCode, targetType, messageKey, args));
	}
	
	public void sendMessageTo(Long targetCode, String targetType, String messageKey, Object... args) {
		this.broadcastMessages.add(new ActionMessages(targetCode, targetType, messageKey, args));
	}
	
	public void talkTo(Long senderCode, Long beingCode, String message) {
		this.broadcastMessages.add(new ActionMessages(senderCode, beingCode, message));
	}
	
	public void describeActor() {
		
		// place
		this.actor.addMessage("{str:YOUAREIN}", actor.getPlace().getPlaceClass().getName());
		this.actor.addMessage("{str:YOUAREINDESC}", actor.getPlace().getPlaceClass().getDescription());
	}

	public void describeTarget() {
		
		switch(this.getTargetType()) {
			case ITEM: {
				
				Item targetItem = (Item)this.target;
			
				this.actor.addMessage("{str:HEREIS}", targetItem.getItemClass().getDescription());
			
				break;
			}
			
			case PLACE: {
				
				PlaceComposite placeComposite = (PlaceComposite)this.target;
				
				placeComposite.getPlace().getPlaceClass().getName();
				placeComposite.getPlace().getPlaceClass().getDescription();
				
				
				break;
			}
			case BEING: {
				
				BeingComposite beingComposite = (BeingComposite)this.target;
				
				if (beingComposite.getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_NON_SENTIENT)) {
					this.actor.addMessage("{str:PACKOFBEINGS}", beingComposite.getBeing().getBeingClass().getName());
				} else if (beingComposite.getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_SENTIENT)) {
					this.actor.addMessage("{str:GROUPOFBEINGS}", beingComposite.getBeing().getBeingClass().getName());
				} else {
					this.actor.addMessage("{str:HEREIS}", beingComposite.getBeing().getName());
				}
				
				break;
			}
		}
		

		// TODO: implement it
	}
	
	public List<ActionMessages> getMessages() {
		return this.broadcastMessages;
	}
	
	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
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
	public Reaction getEffect() {
		return effect;
	}
	public void setEffect(Reaction effect) {
		this.effect = effect;
	}

}
