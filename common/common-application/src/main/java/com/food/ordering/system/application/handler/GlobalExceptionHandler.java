package com.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public ErrorDTO handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("unexpected error").build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(value = ValidationException.class)
    public ErrorDTO handleException(ValidationException validationException) {
        ErrorDTO errorDTO;
        if (validationException instanceof ConstraintViolationException) {
            String violations = extractViolationFromExceptions((ConstraintViolationException) validationException);
            log.error(violations, validationException);
            errorDTO = ErrorDTO.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(violations).build();
        }
        else {
            log.error(validationException.getMessage(), validationException);
            errorDTO = ErrorDTO.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(validationException.getMessage()).build();
        }
        return errorDTO;
    }

    private String extractViolationFromExceptions(ConstraintViolationException validationException) {
        return validationException.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));

    }

}
