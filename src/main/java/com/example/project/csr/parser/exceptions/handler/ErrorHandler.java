package com.example.project.csr.parser.exceptions.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.project.csr.parser.exceptions.model.ErrorResponse;
import com.example.project.csr.parser.exceptions.throwable.InvalidCsrFileExceptions;

/**
 * A centralized exception handler for handling application-specific exceptions
 * and returning structured error responses.
 *
 * <p>This class uses the {@link ControllerAdvice} annotation to globally handle exceptions
 * across the entire application. It extends {@link ResponseEntityExceptionHandler} to provide
 * custom handling for specific exceptions.</p>
 *
 */
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@link InvalidCsrFileExceptions} by returning a {@link ResponseEntity}
     * containing an {@link ErrorResponse} with the exception details and appropriate HTTP status.
     *
     * @param exception the {@link InvalidCsrFileExceptions} thrown by the application
     * @return a {@link ResponseEntity} containing the error response and HTTP status
     */
    @ExceptionHandler(InvalidCsrFileExceptions.class)
    protected ResponseEntity<Object> handleBookNotFoundException(final InvalidCsrFileExceptions exception) {
        return ResponseEntity.status(exception.getResponseStatus()).body(new ErrorResponse(exception.getMessage(),exception.getResponseStatus().toString()));
    }
}
