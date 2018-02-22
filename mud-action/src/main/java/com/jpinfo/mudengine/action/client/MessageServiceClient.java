package com.jpinfo.mudengine.action.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.service.MessageService;

@FeignClient("mud-message")
public interface MessageServiceClient extends MessageService {

}
