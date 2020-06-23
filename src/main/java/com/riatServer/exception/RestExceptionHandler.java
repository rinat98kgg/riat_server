package com.riatServer.exception;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<AwesomeException> handleFileException(ServiceException ex) {
        return new ResponseEntity<>(new AwesomeException(ex.getMessage()), ex.getStatus());
    }

    @Data
    private static class AwesomeException {
        private String message;

        public AwesomeException(String message) {
            this.message = message;
        }
    }

    //other exception handlers below

}