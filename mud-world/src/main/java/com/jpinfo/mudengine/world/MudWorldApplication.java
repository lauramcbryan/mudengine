package com.jpinfo.mudengine.world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MudWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudWorldApplication.class, args);
	}
}
