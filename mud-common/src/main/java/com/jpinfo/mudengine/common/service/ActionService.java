package com.jpinfo.mudengine.common.service;

import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.action.Action;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping("/action")
public interface ActionService {

	@RequestMapping(method=RequestMethod.GET, value="/actor/{actorCode}")
	public Iterable<Action> getActiveActions(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("actorCode") Long actorCode);
	
	@RequestMapping(method=RequestMethod.GET, value="{actionCode}")
	public Action getAction(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("actionCode") Long actionCode);
	
	@RequestMapping(method=RequestMethod.PUT, value="/{verb}")
	public Action insertCommand(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("verb") String verb,
			@RequestParam("actorCode") Long actorCode,
			@RequestParam("mediatorCode") Optional<String> mediatorCode, 
			@RequestParam("mediatorType") Optional<String> mediatorType,
			@RequestParam("targetCode") String targetCode, @RequestParam("targetType") String targetType);

	@RequestMapping(method=RequestMethod.DELETE, value="{actionCode}")
	public void cancelAction(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("actionCode") Long actionCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/actor/{actorCode}")
	public void cancelAllActionFromBeing(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("actorCode") Long actorCode);
	
	@RequestMapping(method=RequestMethod.DELETE, value="/place/{worldName}/{placeCode}")
	public void cancelAllActionFromPlace(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, 
			@PathVariable("worldName") String worldName, @PathVariable("placeCode") Integer placeCode);

	
	
}
