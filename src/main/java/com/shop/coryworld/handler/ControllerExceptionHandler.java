package com.shop.coryworld.handler;

import com.shop.coryworld.exception.NoAuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoAuthorizationException.class)
    public String authorizationException(NoAuthorizationException e) {
        return Script.back(e.getMessage());
    }
}
