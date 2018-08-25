package com.jpinfo.mudengine.common.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.message.Message;

@RequestMapping(path="/message")
public interface MessageService {
	
	
	@RequestMapping(method=RequestMethod.PUT, path="/being/{targetCode}")
	public void putMessage( 
			@PathVariable("targetCode") Long targetCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, 
			@RequestParam(name="senderName", required=false) String senderName, 
			@RequestParam(name="parms", required=false) String...parms);

	@RequestMapping(method=RequestMethod.PUT, path="/being/{targetCode}/plain")
	public void putPlainMessage( 
			@PathVariable("targetCode") Long targetCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, 
			@RequestParam(name="senderName", required=false) String senderName 
			);
	
	@RequestMapping(method=RequestMethod.PUT, path="/place/{placeCode}")
	public void broadcastMessage( 
			@PathVariable("placeCode") Integer placeCode, @RequestParam("message") String message, 
			@RequestParam(name="senderCode", required=false) Long senderCode, 
			@RequestParam(name="senderName", required=false) String senderName, 
			@RequestParam(name="parms", required=false) String...parms);
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Message> getMessage(
			@RequestParam(name="allMessages", defaultValue="false", required=false) Boolean allMessages,
			@RequestParam(name="pageCount", defaultValue="1", required=false) Integer pageCount,
			@RequestParam(name="pageSize", defaultValue="10", required=false) Integer pageSize);
}
