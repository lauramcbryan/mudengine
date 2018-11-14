package com.jpinfo.mudengine.common.message;

import java.util.List;

import lombok.Data;

@Data
public class MessageRequest {

	private Long senderCode;
	
	private String senderName;
	
	private String messageKey;
	
	private String[] args;
	
	private List<MessageEntity> changedEntities;
}
