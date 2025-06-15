package com.damian.whatsapp.auth.exception;

public class AccountDisabledException extends AuthenticationException {
    public AccountDisabledException(String message) {
        super(message);
    }
}
