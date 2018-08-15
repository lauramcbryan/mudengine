package com.jpinfo.mudengine.player;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.player",
		"com.jpinfo.mudengine.common"
})
public class MudPlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudPlayerApplication.class, args);
	}
}
