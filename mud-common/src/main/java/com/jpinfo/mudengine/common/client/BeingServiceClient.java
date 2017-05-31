package com.jpinfo.mudengine.common.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.service.BeingService;

@FeignClient("mud-being")
public interface BeingServiceClient extends BeingService {

}
