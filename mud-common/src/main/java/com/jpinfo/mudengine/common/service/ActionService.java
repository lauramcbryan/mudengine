package com.jpinfo.mudengine.common.service;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.action.Action;

@RequestMapping("/action")
public interface ActionService {

	@GetMapping(value="/actor/{actorCode}")
	public Iterable<Action> getActiveActions(@PathVariable("actorCode") Long actorCode);
	
	@GetMapping(value="{actionCode}")
	public Action getAction(@PathVariable("actionCode") Long actionCode);
	
	@PutMapping(value="/{commandId}")
	public Action insertCommand(@PathVariable("commandId") Integer commandId,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("targetCode") String targetCode);

	@DeleteMapping(value="{actionCode}")
	public void cancelAction(@PathVariable("actionCode") Long actionCode);
	
	@DeleteMapping(value="/actor/{actorCode}")
	public void cancelAllActionFromBeing(@PathVariable("actorCode") Long actorCode);
	
	@PostMapping(value="/update")
	public void updateActions();
}
