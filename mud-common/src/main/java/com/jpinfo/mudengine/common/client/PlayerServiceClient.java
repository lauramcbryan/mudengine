package com.jpinfo.mudengine.common.client;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.jpinfo.mudengine.common.interfaces.PlayerService;

@FeignClient("mud-being")
public interface PlayerServiceClient extends PlayerService {

}
