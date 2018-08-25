package com.jpinfo.mudengine.common.service;

import java.util.Optional;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.action.Action;

@RequestMapping("/action")
public interface ActionService {

	@RequestMapping(method=RequestMethod.GET, value="/actor/{actorCode}")
	public Iterable<Action> getActiveActions(@PathVariable("actorCode") Long actorCode);
	
	@RequestMapping(method=RequestMethod.GET, value="{actionCode}")
	public Action getAction(@PathVariable("actionCode") Long actionCode);
	
	@RequestMapping(method=RequestMethod.PUT, value="/{commandId}")
	public Action insertCommand(@PathVariable("commandId") Integer commandId,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode);

	@RequestMapping(method=RequestMethod.DELETE, value="{actionCode}")
	public void cancelAction(@PathVariable("actionCode") Long actionCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/actor/{actorCode}")
	public void cancelAllActionFromBeing(@PathVariable("actorCode") Long actorCode);
	
	@RequestMapping(method=RequestMethod.POST, value="/update")
	public void updateActions();
}
