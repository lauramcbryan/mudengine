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
import com.jpinfo.mudengine.action.model.MudActionState;
import com.jpinfo.mudengine.action.repository.MudActionRepository;
import com.jpinfo.mudengine.action.repository.MudActionStateRepository;
import com.jpinfo.mudengine.action.utils.ActionHelper;
import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.action.ActionSimpleState;

@RestController
@RequestMapping("/action")
public class ActionController {

	@Autowired
	private MudActionRepository repository;
	
	@Autowired
	private MudActionStateRepository stateRepository;
	
	
	@RequestMapping(method=RequestMethod.GET, value="/actor/{actorCode}")
	public Iterable<ActionSimpleState> getActiveActions(@PathVariable Integer actorCode) {
		
		List<ActionSimpleState> responseList = new ArrayList<ActionSimpleState>();
		
		List<MudActionState> stateList = stateRepository.findByActionIssuerCode(actorCode);
		
		
		for(MudActionState curState: stateList) {
			
			responseList.add(ActionHelper.buildSimpleState(curState));
		}
		
		return responseList;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="{actionCode}")
	public ActionSimpleState getAction(@PathVariable Long actionCode) {
		
		ActionSimpleState response = null;
		
		MudActionState state = stateRepository.findOne(actionCode);
		
		if (state!=null) {
			response = ActionHelper.buildSimpleState(state);
		} else {
			// isn´t created on MudActionState yet.  Let´s call MudAction itself
			MudAction dbAction = repository.findOne(actionCode);
			
			if (dbAction!=null) {
				
				response = new ActionSimpleState();
				response.setActionId(dbAction.getActionId());
			}
		}
		
		return response;
	}	
	
	@RequestMapping(method=RequestMethod.PUT)
	public ActionSimpleState insertCommand(@RequestBody Action newAction) {
		
		MudAction dbAction = ActionHelper.buildMudAction(newAction);
		
		// Save the new command; obtain an actionId
		dbAction = repository.save(dbAction);
		
		ActionSimpleState response = new ActionSimpleState();

		// set the actionId back
		response.setActionId(dbAction.getActionId());
		
		return response;
	}
}
