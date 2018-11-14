package com.jpinfo.mudengine.action.client;

public interface MessageServiceClient {

	public void putMessage( 
			Long targetCode, String message, 
			Long senderCode, String senderName, 
			String...parms);
	
	public void broadcastMessage( 
			Integer placeCode, String message, 
			Long senderCode, String senderName, 
			String...parms);
}
