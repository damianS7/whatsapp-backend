package com.damian.whatsapp.modules.contact.exception;

import com.damian.whatsapp.shared.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class ContactExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ContactExceptionHandler.class);

    @ExceptionHandler({ContactNotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(ContactException ex) {
        log.warn("contact user: {} from user: {} not found.", ex.getContactUserId(), ex.getUserId(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ContactAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleContactAlreadyExists(ContactException ex) {
        log.warn("contact user: {} is already a contact for user: {}", ex.getContactUserId(), ex.getUserId(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(MaxContactsLimitReachedException.class)
    public ResponseEntity<ApiResponse<String>> handleContactLimit(ContactException ex) {
        log.warn(
                "contact user: {} cannot be added for user: {} because limit reached",
                ex.getContactUserId(),
                ex.getUserId(),
                ex
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(ContactAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(ContactException ex) {
        log.warn("contact user: {} for user: {}. operation exception.", ex.getContactUserId(), ex.getUserId(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}