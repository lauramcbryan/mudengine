package com.jpinfo.mudengine.action.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.interfaces.ItemService;

@FeignClient("mud-item")
public interface ItemServiceClient extends ItemService {

}
