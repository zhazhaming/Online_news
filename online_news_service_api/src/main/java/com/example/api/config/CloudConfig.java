package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CloudConfig {

    public CloudConfig() {
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate (new OkHttp3ClientHttpRequestFactory (  ));
    }

}
