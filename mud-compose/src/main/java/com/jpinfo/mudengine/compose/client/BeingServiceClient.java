package com.jpinfo.mudengine.compose.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.BeingService;

@FeignClient(path = "${being.endpoint}")
public interface BeingServiceClient extends BeingService {

}
