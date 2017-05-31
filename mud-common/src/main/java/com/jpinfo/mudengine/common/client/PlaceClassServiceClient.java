package com.jpinfo.mudengine.common.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.service.PlaceClassService;

@FeignClient("mud-world")
public interface PlaceClassServiceClient extends PlaceClassService {

}
