package com.jpinfo.mudengine.being.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.ItemService;

@FeignClient("mud-item")
public interface ItemServiceClient extends ItemService {

}
