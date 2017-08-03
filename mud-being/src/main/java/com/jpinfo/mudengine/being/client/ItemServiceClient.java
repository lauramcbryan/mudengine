package com.jpinfo.mudengine.being.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.service.ItemService;

@FeignClient("mud-item")
public interface ItemServiceClient extends ItemService {

}
