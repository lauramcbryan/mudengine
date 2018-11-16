package com.jpinfo.mudengine.common.message;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MessageRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	

	private Long senderCode;
	
	private String senderName;
	
	private String messageKey;
	
	private String[] args;
	
	private List<MessageEntity> changedEntities;
}
