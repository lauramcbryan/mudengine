package com.jpinfo.mudengine.being.client;

import com.jpinfo.mudengine.common.message.MessageRequest;

public interface MessageServiceClient {

	public void putMessage(Long targetCode, MessageRequest request);
}
