package com.jpinfo.mudengine.action.client;

import org.springframework.cloud.openfeign.FeignClient;

import com.jpinfo.mudengine.common.service.MessageService;

@FeignClient("mud-message")
public interface MessageServiceClient extends MessageService {

}
