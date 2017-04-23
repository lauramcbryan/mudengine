package com.jpinfo.mudengine.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MudActionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudActionApplication.class, args);
	}
}
