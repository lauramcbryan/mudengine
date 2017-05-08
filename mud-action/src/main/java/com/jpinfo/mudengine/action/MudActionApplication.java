package com.jpinfo.mudengine.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
public class MudActionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudActionApplication.class, args);
	}
}
