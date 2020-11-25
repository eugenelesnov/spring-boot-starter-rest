package com.github.eugenelesnov.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Configuration
@ConditionalOnMissingBean(RestTemplate.class)
@EnableConfigurationProperties(SslProperties.class)
@RequiredArgsConstructor
public class AutoConfiguration {

    private final RestTemplateCreator restTemplateCreator;

    @Bean
    public RestTemplate restTemplate() {
        return restTemplateCreator.createRestTemplate();
    }
}