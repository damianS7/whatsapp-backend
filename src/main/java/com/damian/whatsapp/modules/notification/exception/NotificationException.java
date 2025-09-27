package com.damian.whatsapp.modules.notification.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class NotificationException extends ApplicationException {
    private final Long notificationId;
    private final Long customerId;

    public NotificationException(String message) {
        this(message, null, null);
    }

    public NotificationException(String message, Long notificationId, Long customerId) {
        super(message);
        this.customerId = customerId;
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
