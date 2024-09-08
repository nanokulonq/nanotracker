package com.nanokulon.nanotracker.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class MethodNotAllowedAdvice {
    private final MessageSource messageSource;

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception, Locale locale) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED,
                        this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)));
    }
}
