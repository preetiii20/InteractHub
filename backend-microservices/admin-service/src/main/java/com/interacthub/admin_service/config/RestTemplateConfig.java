package com.interacthub.admin_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // <-- This tells Spring this file contains bean definitions
public class RestTemplateConfig {

    @Bean // <-- This annotation creates the RestTemplate object
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}