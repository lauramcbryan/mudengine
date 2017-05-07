package com.jpinfo.mudengine.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MudRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudRegistryApplication.class, args);
	}
}
