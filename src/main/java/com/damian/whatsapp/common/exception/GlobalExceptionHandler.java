package com.damian.whatsapp.common.exception;

import com.damian.whatsapp.auth.exception.*;
import com.damian.whatsapp.common.utils.ApiResponse;
import com.damian.whatsapp.contact.exception.ContactAlreadyExistException;
import com.damian.whatsapp.contact.exception.ContactAuthorizationException;
import com.damian.whatsapp.contact.exception.ContactNotFoundException;
import com.damian.whatsapp.contact.exception.MaxContactsLimitReachedException;
import com.damian.whatsapp.customer.exception.CustomerEmailTakenException;
import com.damian.whatsapp.customer.exception.CustomerException;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import com.damian.whatsapp.customer.profile.exception.ProfileAuthorizationException;
import com.damian.whatsapp.customer.profile.exception.ProfileException;
import com.damian.whatsapp.customer.profile.exception.ProfileNotFoundException;
import com.damian.whatsapp.group.exception.GroupAuthorizationException;
import com.damian.whatsapp.group.exception.GroupNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation error", errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(
            {
                    AuthenticationException.class,
                    JwtAuthenticationException.class,
                    AuthenticationBadCredentialsException.class,
                    AccountDisabledException.class
            }
    )
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(
            {
                    EntityNotFoundException.class,
                    CustomerNotFoundException.class,
                    ProfileNotFoundException.class,
                    ContactNotFoundException.class,
                    GroupNotFoundException.class
            }
    )
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(ApplicationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(
            {
                    CustomerEmailTakenException.class,
                    MaxContactsLimitReachedException.class,
                    MaxUploadSizeExceededException.class,
                    ContactAlreadyExistException.class

            }
    )
    public ResponseEntity<ApiResponse<String>> handleConflitException(ApplicationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(
            {
                    ApplicationException.class,
                    ProfileException.class,
                    CustomerException.class,
            }
    )
    public ResponseEntity<ApiResponse<String>> handleApplicationException(ApplicationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(
            {
                    RuntimeException.class,
                    Exception.class
            }
    )
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(
            {
                    AuthorizationException.class,
                    ContactAuthorizationException.class,
                    GroupAuthorizationException.class,
                    ProfileAuthorizationException.class,
                    PasswordMismatchException.class
            }
    )
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(ApplicationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}