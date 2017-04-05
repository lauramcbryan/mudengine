package com.jpinfo.mudengine.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.jpinfo.mudengine.item.repository")
@EntityScan("com.jpinfo.mudengine.item.model")
public class MudItemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudItemApplication.class, args);
	}
}
