package com.damian.whatsapp.modules.user.token.exception;

public class UserAccountTokenNotFoundException extends UserAccountTokenException {
    public UserAccountTokenNotFoundException(String message, String token, Long userId) {
        super(message, token, userId);
    }
}
