package com.jpinfo.mudengine.action.client;

import org.springframework.stereotype.Component;

import com.jpinfo.mudengine.common.utils.BaseServiceClient;

@Component
public class MessageServiceClientImpl extends BaseServiceClient implements MessageServiceClient {

	@Override
	public void putMessage(Long targetCode, String message, Long senderCode, String senderName, String... parms) {
		// TODO Auto-generated method stub
		
	}

}
