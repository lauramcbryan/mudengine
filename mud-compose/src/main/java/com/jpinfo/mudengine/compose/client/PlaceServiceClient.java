package com.jpinfo.mudengine.compose.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.PlaceService;

@FeignClient(path = "${place.endpoint}")
public interface PlaceServiceClient extends PlaceService {

}
