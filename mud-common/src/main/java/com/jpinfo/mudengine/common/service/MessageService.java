package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.security.TokenService;

@RequestMapping(path="/message")
public interface MessageService {
	
	
	@RequestMapping(method=RequestMethod.PUT, path="/being/{targetCode}")
	public void putMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("targetCode") Long targetCode, @RequestParam("message") String messageKey, @RequestParam(name="parms") String...parms);

	@RequestMapping(method=RequestMethod.PUT, path="/place/{placeCode}")
	public void broadcastMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken, @PathVariable("placeCode") Integer placeCode, @RequestParam("message") String messageKey, @RequestParam(name="parms") String...parms);
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Message> getMessage(@RequestHeader(TokenService.HEADER_TOKEN) String authToken);
}
