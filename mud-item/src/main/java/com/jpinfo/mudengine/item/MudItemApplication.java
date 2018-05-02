package com.jpinfo.mudengine.item;

import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MudItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudItemApplication.class, args);
	}	
}
