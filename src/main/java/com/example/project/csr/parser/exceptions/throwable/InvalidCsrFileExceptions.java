package com.example.project.csr.parser.exceptions.throwable;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class representing an invalid CSR (Certificate Signing Request) file error.
 * This exception is thrown when there is an issue with the provided CSR file.
 * It returns a {@code HttpStatus.BAD_REQUEST} response by default unless otherwise specified.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCsrFileExceptions extends RuntimeException {
    private HttpStatus responseStatus;

    /**
     * Constructs a new {@code InvalidCsrFileExceptions} with the specified detail message
     * and HTTP response status.
     *
     * @param message the detail message for the exception
     * @param responseStatus the HTTP status to be associated with this exception
     */
    public InvalidCsrFileExceptions(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

    /**
     * Retrieves the HTTP response status for this exception.
     * If {@code responseStatus} is not explicitly set, it attempts to retrieve the status
     * from the {@link ResponseStatus} annotation on the class.
     *
     * @return the HTTP status associated with this exception, or {@code null} if none is set
     */
    public HttpStatus getResponseStatus() {
        if (this.responseStatus != null) {
            return this.responseStatus;
        } else {
            ResponseStatus annotation = this.getClass().getAnnotation(ResponseStatus.class);
            return annotation != null ? annotation.value() : null;
        }
    }
}
