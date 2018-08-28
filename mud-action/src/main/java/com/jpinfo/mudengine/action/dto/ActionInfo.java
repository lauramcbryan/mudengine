package com.jpinfo.mudengine.action.dto;


import java.util.ArrayList;
import java.util.List;

import com.jpinfo.mudengine.action.interfaces.ActionTarget;
import com.jpinfo.mudengine.action.utils.ActionMessage;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.item.Item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ActionInfo extends Action {
	
	private BeingComposite actor;
	
	private Item mediator;
	
	private ActionTarget target;
	
	private Double successRate;
	
	private List<ActionMessage> messages;
	
	public ActionInfo() {
		this.messages = new ArrayList<>();
	}
	
	public boolean hasInitiated() {
		return !getCurState().equals(Action.EnumActionState.NOT_STARTED);
	}
	
	public boolean needToApplyEffects(Long currentTurn) {
		
		return this.getRunType().equals(Action.EnumRunningType.CONTINUOUS) ||
				(currentTurn >=super.getEndTurn());
	}
	
	public boolean isFinished(Long currentTurn) {
		return (super.getEndTurn() != null) && 
				(super.getEndTurn() <= currentTurn) && 
				!this.getRunType().equals(Action.EnumRunningType.CONTINUOUS);
	}
	
	
	public void addMessage(Long senderCode, Long targetCode, String targetType, String messageKey, String... parms) {
		
		this.messages.add(new ActionMessage(senderCode, targetCode, targetType, messageKey, parms));
	}
	
	public void addMessage(Long senderCode, Long targetCode, String targetType, String plainMessage) {
		
		this.messages.add(new ActionMessage(senderCode, targetCode, targetType, plainMessage));
	}
}
