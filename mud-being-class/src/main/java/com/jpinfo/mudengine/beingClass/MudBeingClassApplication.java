package com.jpinfo.mudengine.beingClass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MudBeingClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudBeingClassApplication.class, args);
	}
}
