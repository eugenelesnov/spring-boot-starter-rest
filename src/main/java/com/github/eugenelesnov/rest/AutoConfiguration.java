package com.github.eugenelesnov.rest;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", socketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .setConnectionManager(connectionManager)
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
            sslProperties.setKeyStorePassword(null);
            sslProperties.setTrustStorePassword(null);
        }
    }
}