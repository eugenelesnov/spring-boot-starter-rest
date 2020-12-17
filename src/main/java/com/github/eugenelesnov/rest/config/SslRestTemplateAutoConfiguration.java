package com.github.eugenelesnov.rest.config;

import com.github.eugenelesnov.rest.SslProperties;
import com.github.eugenelesnov.rest.exception.SslContextException;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class SslRestTemplateAutoConfiguration {

    private final SslProperties sslProperties;

    @Bean
    @ConditionalOnProperty(value = "server.ssl.enabled", havingValue = "true")
    public RestTemplate restTemplate() {
        if (sslProperties.checkWhetherSslParametersArePresent()) {
            return createSslRestTemplate();
        }
        throw new SslContextException("Missing required SSL parameters");
    }

    private RestTemplate createSslRestTemplate() {
        SSLContext sslContext = buildSslContext();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", socketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setDefaultMaxPerRoute(sslProperties.getConnectionDefaultMaxPerRoute());

        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
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
            throw new SslContextException("An error occurred while building SSL context", ex);
        } finally {
            // For security issues
            sslProperties.setKeyStorePassword(null);
            sslProperties.setTrustStorePassword(null);
        }
    }
}
