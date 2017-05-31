package com.jpinfo.mudengine.common.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.service.PlaceService;

@FeignClient("mud-world")
public interface PlaceServiceClient extends PlaceService {

}
