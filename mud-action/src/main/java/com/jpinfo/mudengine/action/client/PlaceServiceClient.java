package com.jpinfo.mudengine.action.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.PlaceService;

@FeignClient("mud-world")
public interface PlaceServiceClient extends PlaceService {

}
