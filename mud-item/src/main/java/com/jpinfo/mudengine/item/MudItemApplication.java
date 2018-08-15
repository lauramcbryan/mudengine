package com.jpinfo.mudengine.item;

import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {
		"com.jpinfo.mudengine.item",
		"com.jpinfo.mudengine.common"
})
public class MudItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudItemApplication.class, args);
	}	
}
