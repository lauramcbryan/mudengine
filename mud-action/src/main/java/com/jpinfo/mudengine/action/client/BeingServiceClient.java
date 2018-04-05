package com.jpinfo.mudengine.action.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.BeingService;

@FeignClient("mud-being")
public interface BeingServiceClient extends BeingService {

}
