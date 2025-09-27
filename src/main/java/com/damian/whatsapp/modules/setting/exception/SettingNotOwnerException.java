package com.damian.whatsapp.modules.setting.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class SettingNotOwnerException extends ApplicationException {
    private final Long customerId;

    public SettingNotOwnerException(String message) {
        this(message, null);
    }

    public SettingNotOwnerException(String message, Long customerId) {
        super(message);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
