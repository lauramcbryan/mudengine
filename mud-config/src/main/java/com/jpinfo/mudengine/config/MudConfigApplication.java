package com.jpinfo.mudengine.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MudConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudConfigApplication.class, args);
	}
}
