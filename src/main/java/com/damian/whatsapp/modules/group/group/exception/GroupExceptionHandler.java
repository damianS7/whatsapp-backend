package com.damian.whatsapp.modules.group.group.exception;

import com.damian.whatsapp.modules.group.member.exception.GroupMemberNotFoundException;
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
public class GroupExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GroupExceptionHandler.class);

    @ExceptionHandler({GroupNotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(GroupException ex) {
        log.warn("group: {} not found.", ex.getGroupId(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({GroupMemberNotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(GroupMemberNotFoundException ex) {
        log.warn("group: {} user member: {} not found.", ex.getGroupId(), ex.getGroupMemberId(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(GroupAuthorizationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationException(GroupException ex) {
        log.warn("group {}. operation exception.", ex.getGroupId(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN));
    }
}