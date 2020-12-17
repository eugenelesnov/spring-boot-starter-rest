package com.github.eugenelesnov.rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnMissingBean(RestTemplate.class)
@RequiredArgsConstructor
public class RestTemplateAutoConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    @ConditionalOnProperty(value = "server.ssl.enabled", matchIfMissing = true)
    public RestTemplate restTemplate() {
        return restTemplateBuilder.build();
    }
}