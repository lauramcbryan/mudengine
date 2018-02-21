package com.jpinfo.mudengine.action.dto;

import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action.EnumTargetType;
import com.jpinfo.mudengine.common.being.Being;
import com.jpinfo.mudengine.common.being.BeingAttrModifier;
import com.jpinfo.mudengine.common.being.BeingSkillModifier;
import com.jpinfo.mudengine.common.item.Item;
import com.jpinfo.mudengine.common.place.Place;

public class BeingComposite implements ActionTarget  {

	private Being being;
	
	private List<Item> items;
	
	private Place place;
	
	private List<ActionMessage> messages;
	
	public BeingComposite() {
		this.messages = new ArrayList<ActionMessage>();		
	}
	
	public BeingComposite(Being simpleBeing) {
		this.being = simpleBeing;
		
		this.messages = new ArrayList<ActionMessage>();
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
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

	public void addMessage(String messageKey, String... parms) {
		
		this.addMessage(null,  messageKey, parms);
	}

	@Override
	public void describeIt(ActionTarget target) {

		if (getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_NON_SENTIENT)) {
			
			if (getBeing().getQuantity()>1) {
				target.addMessage("{str:PACKOFBEINGS}", getBeing().getBeingClass().getName());				
			} else {
				target.addMessage("{str:SIMPLESTR}", getBeing().getBeingClass().getName());				
			}
			
		} else if (getBeing().getBeingType().equals(Being.BEING_TYPE_REGULAR_SENTIENT)) {
			
			if (getBeing().getQuantity()>1) {
				target.addMessage("{str:GROUPOFBEINGS}", getBeing().getBeingClass().getName());
			} else {
				target.addMessage("{str:SIMPLESTR}", getBeing().getBeingClass().getName());
			}
			
		} else {
			target.addMessage("{str:SIMPLEBEING}", getBeing().getBeingClass().getName(), getBeing().getName());
		}
		

	}
	
	public void describeYourself() {
		
		this.addMessage("{str:YOUARE}", this.getBeing().getName(), this.getBeing().getBeingClass().getName());	
		this.addMessage("{str:YOUAREDESC}", this.getBeing().getBeingClass().getDescription());
		
		// =========== ATTRIBUTES ==========
		this.addMessage("{str:ATTRHEADER}");
		
		for(String curAttr: this.getBeing().getAttrs().keySet()) {
			
			Float attrModifier = 0.0F;
			
			for (BeingAttrModifier curModifier: this.getBeing().getAttrModifiers()) {
				if (curModifier.getAttribute().equals(curAttr)) {
					
					attrModifier += curModifier.getOffset();
				}
			}
			
			if (attrModifier == 0.0F) {
				this.addMessage("{str:ATTR}", curAttr, String.valueOf(this.getBeing().getAttrs().get(curAttr)));				
			}
			else {
				this.addMessage("{str:ATTRMOD}", curAttr, 
						String.valueOf(this.getBeing().getAttrs().get(curAttr)),
						String.valueOf(attrModifier)
						);
			}
		}
		
		// =========== SKILLS ==========
		this.addMessage("{str:SKILLHEADER}");

		for(String curSkill: this.getBeing().getSkills().keySet()) {
			
			Float skillModifier = 0.0F;
			
			for(BeingSkillModifier curModifier: this.getBeing().getSkillModifiers()) {
				
				if (curModifier.getSkillCode().equals(curSkill)) {
					skillModifier += curModifier.getOffset();
				}
			}
			
			if (skillModifier == 0.0F) {
				this.addMessage("{str:SKILL}", curSkill, String.valueOf(this.getBeing().getSkills().get(curSkill)));
			} else {
				
				this.addMessage("{str:SKILLMOD}", curSkill, 
						String.valueOf(this.getBeing().getSkills().get(curSkill)),
						String.valueOf(skillModifier)
						);
			}
			
		}
		
		// =========== ITEMS ==========
		this.addMessage("{str:YOUHAVEHEADER}");

		for(Item curItem: this.getItems()) {
			this.addMessage("{str:SIMPLESTR}", curItem.getItemClass().getDescription());
		}
		
		if (this.getItems().isEmpty()) {
			this.addMessage("{str:NOTHING}");
		}
	}
	
}
