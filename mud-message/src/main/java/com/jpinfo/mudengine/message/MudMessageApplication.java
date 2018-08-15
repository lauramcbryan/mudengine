package com.jpinfo.mudengine.message;

import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.message",
		"com.jpinfo.mudengine.common"
})
public class MudMessageApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudMessageApplication.class, args);
	}
}
