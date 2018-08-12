package com.jpinfo.mudengine.player;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.player",
		"com.jpinfo.mudengine.common"
})
@EnableDiscoveryClient
public class MudPlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudPlayerApplication.class, args);
	}
}
