package com.interacthub.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
		System.out.println("Manager Microservice is running on port 8083...");
    }
    
    // CRITICAL: Defines the RestTemplate bean for inter-service HTTP calls
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}