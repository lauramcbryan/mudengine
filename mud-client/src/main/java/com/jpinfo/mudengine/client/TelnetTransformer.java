package com.jpinfo.mudengine.client;

import java.util.Arrays;

import org.springframework.integration.transformer.ObjectToStringTransformer;

public class TelnetTransformer extends ObjectToStringTransformer {

	@Override
	protected String transformPayload(Object payload) {
		
		if (payload instanceof byte[]) {
			
			byte[] byteArrayPayload = (byte[])payload;
			
			// Check if there's control characters at start of payload
			int k=0;
			for(;(k<byteArrayPayload.length) && (byteArrayPayload[k] < 32);k++);
			
			// Call super method passing just the remaining buffer
			return super.transformPayload(
					Arrays.copyOfRange(byteArrayPayload, k, byteArrayPayload.length)
					);
			
		}
		
		return super.transformPayload(payload);
	}

}
