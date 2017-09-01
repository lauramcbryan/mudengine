package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.service.ActionService;

@RestController
@RequestMapping("/action")
public class ActionController implements ActionService {

	@Autowired
	private MudActionRepository repository;

	@Override
	public Iterable<Action> getActiveActions(@RequestHeader String authToken, @PathVariable Long actorCode) {
		
		List<Action> responseList = new ArrayList<Action>();
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);
		
		
		for(MudAction curState: stateList) {
			
			responseList.add(ActionHelper.buildAction(curState));
		}
		
		return responseList;
	}

	@Override
	public Action getAction(@RequestHeader String authToken, @PathVariable Long actionCode) {
		
		Action response = null;
		
		MudAction state = repository.findOne(actionCode);
		
		if (state!=null) {
			response = ActionHelper.buildAction(state);
		}
		
		return response;
	}	
	
	@Override
	public Action insertCommand(@RequestHeader String authToken, @RequestBody Action newAction) {
		
		Action response = null;
		
		MudAction dbAction = new MudAction(); 
		
		dbAction.setActorCode(newAction.getActorCode());
		dbAction.setIssuerCode(newAction.getIssuerCode());
		dbAction.setActionCode(newAction.getActionCode());
		dbAction.setMediatorCode(newAction.getMediatorCode());
		dbAction.setPlaceCode(newAction.getPlaceCode());
		dbAction.setTargetCode(newAction.getTargetCode());
		dbAction.setTargetType(newAction.getTargetType().ordinal());
		dbAction.setWorldName(newAction.getWorldName());
		dbAction.setCurrState(Action.EnumActionState.NOT_STARTED);
		
		// Save the new command; obtain an actionId
		dbAction = repository.save(dbAction);
		
		response = ActionHelper.buildAction(dbAction);
		
		return response;
	}
	
	@Override
	public void cancelAction(@RequestHeader String authToken, @PathVariable Long actionCode) {
		
		MudAction dbAction = repository.findActiveOne(actionCode);
		
		if (dbAction!=null) {
			
			dbAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(dbAction);
		}
	}

	@Override
	public void cancelAllActionFromBeing(@RequestHeader String authToken, @PathVariable Long actorCode) {
		
		List<MudAction> dbActionList = repository.findActiveByActorCode(actorCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}

	@Override
	public void cancelAllActionFromPlace(@RequestHeader String authToken, @PathVariable String worldName, @PathVariable Integer placeCode) {
		
		List<MudAction> dbActionList = repository.findActiveByPlace(worldName, placeCode);
		
		for(MudAction curAction: dbActionList) {
			
			curAction.setCurrState(Action.EnumActionState.CANCELLED);
			
			repository.save(curAction);
		}
	}
	
}
