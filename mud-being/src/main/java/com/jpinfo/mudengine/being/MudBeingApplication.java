package com.jpinfo.mudengine.being;

import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.being",
		"com.jpinfo.mudengine.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class MudBeingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudBeingApplication.class, args);
	}
}
