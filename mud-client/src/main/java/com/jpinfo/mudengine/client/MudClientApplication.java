package com.jpinfo.mudengine.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.jpinfo.mudengine.client.service.ConsoleAppService;

@SpringBootApplication
@EnableFeignClients
public class MudClientApplication {
	
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	public CommandLineRunner startApp(TaskExecutor executor) {
		
		return new CommandLineRunner() {
			
			public void run(String... args) throws Exception {
				executor.execute(new ConsoleAppService());
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MudClientApplication.class, args);
	}
}
