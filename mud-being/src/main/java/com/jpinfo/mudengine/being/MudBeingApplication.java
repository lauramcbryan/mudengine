package com.jpinfo.mudengine.being;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MudBeingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudBeingApplication.class, args);
	}
}
