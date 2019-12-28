package com.jpinfo.mudengine.message.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpinfo.mudengine.common.message.Message;
import com.jpinfo.mudengine.common.message.MessageRequest;
import com.jpinfo.mudengine.common.service.MessageService;
import com.jpinfo.mudengine.message.service.MessageServiceImpl;

@RestController
public class MessageController implements MessageService {
	
	@Autowired
	private MessageServiceImpl service;
	
	@Override
	public ResponseEntity<Long> putMessage( 
			@PathVariable("targetCode") Long targetCode,
			@RequestBody MessageRequest request) {
		
		return new ResponseEntity<>(
				service.putMessage(targetCode, request), 
				HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<List<Long>> broadcastMessage( 
			@PathVariable("placeCode") Integer placeCode, 
			@RequestBody MessageRequest request) {
		
		return new ResponseEntity<>(
				service.broadcastMessage(placeCode, request), 
				HttpStatus.CREATED);
	}
	
	
	@Override
	public List<Message> getMessage(
			@RequestParam(name="allMessages", defaultValue="false", required=false) Boolean allMessages,
			@RequestParam(name="pageCount", defaultValue="0", required=false) Integer pageCount,
			@RequestParam(name="pageSize", defaultValue="10", required=false) Integer pageSize) {

		return service.getMessage(allMessages, pageCount, pageSize);
	}


}
