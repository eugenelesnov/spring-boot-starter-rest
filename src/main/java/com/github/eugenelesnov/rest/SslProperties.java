package com.github.eugenelesnov.rest;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.github.eugenelesnov.rest.Utils.isNullOrEmpty;


@ToString(exclude = {"keyStorePassword", "trustStorePassword"})
@Data
@Component
@ConfigurationProperties(prefix = "server.ssl")
public class SslProperties {

    /**
     * NB! If this parameter is true, but other parameters are not set,
     * an exception will be thrown
     */
    private boolean enabled;
    private String keyStore;
    private String keyStorePassword;
    private String keyAlias;
    private String trustStore;
    private String trustStorePassword;
    private Integer connectionDefaultMaxPerRoute = 25;

    public boolean checkWhetherSslParametersArePresent() {
        return checkIfKeystoreParametersArePresent() && checkIfTruststoreParametersArePresent();
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword.toCharArray();
    }

    private boolean checkIfTruststoreParametersArePresent() {
        return !isNullOrEmpty(trustStore) && trustStorePassword != null;
    }

    private boolean checkIfKeystoreParametersArePresent() {
        return !isNullOrEmpty(keyStore) && keyStorePassword != null && !isNullOrEmpty(keyAlias);
    }
}