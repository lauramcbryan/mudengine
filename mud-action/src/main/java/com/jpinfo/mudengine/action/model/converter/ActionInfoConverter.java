package com.jpinfo.mudengine.action.model.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.action.client.ItemServiceClient;
import com.jpinfo.mudengine.action.dto.ActionInfo;
import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.model.MudActionClass;
import com.jpinfo.mudengine.action.repository.MudActionClassRepository;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionClass;
import com.jpinfo.mudengine.common.exception.EntityNotFoundException;
import com.jpinfo.mudengine.common.utils.LocalizedMessages;

@Component
public class ActionInfoConverter {
	
	@Autowired
	private MudActionClassRepository classRepository;	
	
	@Autowired
	private ItemServiceClient itemService;
	
	@Autowired
	private BeingCompositeConverter beingConverter;
	
	@Autowired
	private PlaceCompositeConverter placeConverter;
	
	@Autowired
	private ItemCompositeConverter itemConverter;

	
	public ActionInfo build(MudAction dbAction) {
		
		ActionInfo result = new ActionInfo();
		
		result.setActionId(dbAction.getActionId());
		
		result.setCurState(Action.EnumActionState.values()[dbAction.getCurrState()]);
		result.setStartTurn(dbAction.getStartTurn());
		result.setEndTurn(dbAction.getEndTurn());
		
		// Solving the actionClass
		result.setActionClassCode(dbAction.getActionClassCode());
		result.setActionClass(getActionClass(dbAction));

		
		//Actor
		result.setActorCode(dbAction.getActorCode());
		result.setActor(beingConverter.build(dbAction.getActorCode()));
		
		// Mediator
		if (dbAction.getMediatorCode()!=null) {
			result.setMediatorCode(dbAction.getMediatorCode());
			result.setMediator(itemService.getItem(Long.valueOf(dbAction.getMediatorCode())));
		}

		// Target
		result.setTargetCode(dbAction.getTargetCode());
		
		switch(Action.EnumTargetType.valueOf(dbAction.getTargetType())) {
			case ITEM:
				result.setTarget(itemConverter.build(Long.valueOf(dbAction.getTargetCode())));
				break;
				
			case PLACE: 
				result.setTarget(placeConverter.build(
						dbAction.getWorldName(), 
						Integer.valueOf(dbAction.getTargetCode())));
				break;
				
			case BEING:
				result.setTarget(beingConverter.build(Long.valueOf(dbAction.getTargetCode())));
				break;
				
			default: 
		}

		
		return result;
	}
	
	
	private ActionClass getActionClass(MudAction dbAction) {
		
		MudActionClass dbActionClass = classRepository
				.findById(dbAction.getActionClassCode())
				.orElseThrow(() -> new EntityNotFoundException(LocalizedMessages.BEING_NOT_FOUND));

		return ActionClassConverter.convert(dbActionClass);
		
	}
}
