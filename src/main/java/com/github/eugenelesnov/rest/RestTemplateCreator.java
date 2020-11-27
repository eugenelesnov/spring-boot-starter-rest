package com.github.eugenelesnov.rest;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;
import java.util.Arrays;

/**
 * @author Eugene Lesnov
 */
@Component
@RequiredArgsConstructor
public class RestTemplateCreator {

    private final RestTemplateBuilder restTemplateBuilder;
    private final SslProperties sslProperties;

    public RestTemplate createRestTemplate() {
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

    private RestTemplate createRegularRestTemplate() {
        return restTemplateBuilder
            .build();
    }
}
