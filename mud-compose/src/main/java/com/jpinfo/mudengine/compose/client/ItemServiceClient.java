package com.jpinfo.mudengine.compose.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.ItemService;

@FeignClient(path = "${item.endpoint}")
public interface ItemServiceClient extends ItemService {

}
