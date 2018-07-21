package com.jpinfo.mudengine.message;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.message",
		"com.jpinfo.mudengine.common"
})
@EnableDiscoveryClient
@EnableFeignClients
public class MudMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudMessageApplication.class, args);
	}
}
