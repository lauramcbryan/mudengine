package com.jpinfo.mudengine.message.client;

import java.util.List;

import com.jpinfo.mudengine.common.being.Being;

public interface BeingServiceClient {

	List<Being> getAllFromPlace(String worldName, Integer placeCode);
}
