package com.jpinfo.mudengine.action.client;

import com.jpinfo.mudengine.common.message.MessageRequest;

public interface MessageServiceClient {

	public void putMessage( 
			Long targetCode, MessageRequest request);
	
	public void broadcastMessage( 
			Integer placeCode, MessageRequest request);
}
