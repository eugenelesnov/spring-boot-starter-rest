package com.github.eugenelesnov.rest;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;
import java.util.Arrays;

@Configuration
@ConditionalOnMissingBean(RestTemplate.class)
@EnableConfigurationProperties(SslProperties.class)
@RequiredArgsConstructor
public class AutoConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;
    private final SslProperties sslProperties;

    @Bean
    public RestTemplate restTemplate() {
        if (sslProperties.isEnabled() && sslProperties.checkWhetherSslParametersArePresent()) {
            return createSslRestTemplate();
        }
        return createRegularRestTemplate();
    }

    private RestTemplate createSslRestTemplate() {
        SSLContext sslContext = buildSslContext();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }

    private RestTemplate createRegularRestTemplate() {
        return restTemplateBuilder.build();
    }

    private SSLContext buildSslContext() {
        try {
            char[] keyStorePassword = sslProperties.getKeyStorePassword();
            return new SSLContextBuilder()
                    .loadKeyMaterial(
                            KeyStore.getInstance(new File(sslProperties.getKeyStore()), keyStorePassword),
                            keyStorePassword
                    ).build();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to instantiate SSL context", ex);
        } finally {
            Arrays.fill(sslProperties.getKeyStorePassword(), (char) 0);
        }
    }
}