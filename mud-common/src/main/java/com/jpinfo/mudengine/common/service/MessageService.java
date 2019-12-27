package com.jpinfo.mudengine.common.service;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.message.MessageRequest;

@RequestMapping(path="/message")
public interface MessageService {
	
	
	@PutMapping(path="/being/{targetCode}")
	public ResponseEntity<Long> putMessage( 
			@PathVariable("targetCode") Long targetCode, 
			@RequestBody MessageRequest request);
	
	@PutMapping(path="/place/{placeCode}")
	public ResponseEntity<List<Long>> broadcastMessage( 
			@PathVariable("placeCode") Integer placeCode, 
			@RequestBody MessageRequest request);
	
	@GetMapping()
	public List<Message> getMessage(
			@RequestParam(name="allMessages", defaultValue="false", required=false) Boolean allMessages,
			@RequestParam(name="pageCount", defaultValue="1", required=false) Integer pageCount,
			@RequestParam(name="pageSize", defaultValue="10", required=false) Integer pageSize);
}
