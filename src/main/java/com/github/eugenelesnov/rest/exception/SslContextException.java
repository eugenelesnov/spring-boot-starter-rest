package com.github.eugenelesnov.rest.exception;

public class SslContextException extends IllegalStateException {

    private static final String ERROR_MESSAGE = "Unable to instantiate SSL context: ";

    public SslContextException(String message) {
        super(ERROR_MESSAGE + message);
    }

    public SslContextException(String message, Throwable cause) {
        super(ERROR_MESSAGE + message, cause);
    }
}
