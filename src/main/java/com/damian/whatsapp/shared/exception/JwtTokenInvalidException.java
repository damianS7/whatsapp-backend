package com.damian.whatsapp.shared.exception;

import org.springframework.security.core.AuthenticationException;

// Remove and use ExpiredJwtException instead
public class JwtTokenInvalidException extends AuthenticationException {
    public JwtTokenInvalidException(String message) {
        super(message);
    }
}
