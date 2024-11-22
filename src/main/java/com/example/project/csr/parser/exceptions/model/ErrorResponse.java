package com.example.project.csr.parser.exceptions.model;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A data class representing the structure of an error response.
 * This is typically used to convey error details in API responses when exceptions occur.
 *
 */
@Data
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String exceptionErrorCode;

    /**
     * Constructs an {@code ErrorResponse} with the specified error message and error code.
     *
     * @param message the message describing the error
     * @param exceptionErrorCode a code identifying the exception type
     */
    public ErrorResponse( String message, String exceptionErrorCode) {
        this.message = message;
        this.exceptionErrorCode = exceptionErrorCode;
    }
}
