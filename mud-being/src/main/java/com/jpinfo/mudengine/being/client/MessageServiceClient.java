package com.jpinfo.mudengine.being.client;

public interface MessageServiceClient {

	public void putMessage(Long targetCode, String message, String...parms);
}
