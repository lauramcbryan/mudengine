package com.jpinfo.mudengine.action.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.action.model.MudAction;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionSimpleState;

@RestController
@RequestMapping("/action")
public class ActionController {

	@Autowired
	private MudActionRepository repository;
	
	@RequestMapping(method=RequestMethod.GET, value="/actor/{actorCode}")
	public Iterable<ActionSimpleState> getActiveActions(@PathVariable Long actorCode) {
		
		List<ActionSimpleState> responseList = new ArrayList<ActionSimpleState>();
		
		List<MudAction> stateList = repository.findByIssuerCode(actorCode);
		
		
		for(MudAction curState: stateList) {
			
			responseList.add(ActionHelper.buildSimpleState(curState));
		}
		
		return responseList;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="{actionCode}")
	public ActionSimpleState getAction(@PathVariable Long actionCode) {
		
		ActionSimpleState response = null;
		
		MudAction state = repository.findOne(actionCode);
		
		if (state!=null) {
			response = ActionHelper.buildSimpleState(state);
		}
		
		return response;
	}	
	
	@RequestMapping(method=RequestMethod.PUT)
	public ActionSimpleState insertCommand(@RequestBody Action newAction) {
		
		ActionSimpleState response = new ActionSimpleState();
		
		MudAction dbAction = ActionHelper.buildMudAction(newAction);
		
		// Save the new command; obtain an actionId
		dbAction = repository.save(dbAction);

		// set the actionId back
		response.setActionId(dbAction.getActionId());
		
		return response;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="{actionCode}")
	public ActionSimpleState cancelAction(@PathVariable Long actionCode) {
		
		ActionSimpleState response = new ActionSimpleState();
		
		MudAction dbAction = repository.findOne(actionCode);
		
		if (dbAction!=null) {
			
			dbAction.setCurrState(ActionSimpleState.CANCELLED);
			
			response = ActionHelper.buildSimpleState(dbAction);
			
			repository.save(dbAction);
		}

		return response;
	}
}
