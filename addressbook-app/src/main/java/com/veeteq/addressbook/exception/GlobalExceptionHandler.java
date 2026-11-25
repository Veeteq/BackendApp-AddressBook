package com.veeteq.addressbook.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class.getSimpleName());

    @ExceptionHandler(ContactNotFoundException.class)
    public ProblemDetail handleContactNotFoundException(ContactNotFoundException exception) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Contact not found");
        problemDetail.setProperty("code", ErrorCode.CONTACT_NOT_FOUND);
        return problemDetail;
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ProblemDetail handleConcurrentModificationException(ConcurrentModificationException exception) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problemDetail.setTitle("Concurrent modification");
        problemDetail.setProperty("code", ErrorCode.CONCURRENT_MODIFICATION);
        return problemDetail;
    }

    @ExceptionHandler(ContactTypeChangeException.class)
    public ProblemDetail handleContactTypeChangeException(ContactTypeChangeException exception) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle("Contact type change not allowed");
        problemDetail.setProperty("code", ErrorCode.CONTACT_TYPE_CHANGE_NOT_ALLOWED);
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(),"Invalid value"), (first, second) -> first));

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Request validation failed");

        problemDetail.setTitle("Validation failed");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("code", ErrorCode.VALIDATION_FAILED);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Request body could not be read");
        problemDetail.setTitle("Invalid request body");
        problemDetail.setProperty("code", ErrorCode.INVALID_REQUEST_BODY);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        LOG.error("Unhandled exception", exception);

        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred");

        problemDetail.setTitle("Internal server error");
        problemDetail.setProperty("code", ErrorCode.INTERNAL_SERVER_ERROR);

        return problemDetail;
    }
}
