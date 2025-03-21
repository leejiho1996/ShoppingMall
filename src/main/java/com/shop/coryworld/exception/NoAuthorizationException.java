package com.shop.coryworld.exception;

import java.util.Map;

public class NoAuthorizationException extends RuntimeException {
    public NoAuthorizationException(String message) {
        super(message);
    }
}
