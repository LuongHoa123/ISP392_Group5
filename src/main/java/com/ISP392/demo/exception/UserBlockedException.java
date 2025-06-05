package com.ISP392.demo.exception;

import org.springframework.security.core.AuthenticationException;

public class UserBlockedException extends AuthenticationException {
    public UserBlockedException(String msg) {
        super(msg);
    }
}
