package com.github.eugenelesnov.rest.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HostnameVerifier;

@Configuration
public class HostnameVerifierAutoConfiguration {

    /**
     * Creates HostnameVerifier instance with turned off hostname verification
     *
     * @return NoopHostnameVerifier instance
     */
    @Bean
    @ConditionalOnMissingBean(HostnameVerifier.class)
    public HostnameVerifier hostnameVerifier() {
        return NoopHostnameVerifier.INSTANCE;
    }
}
