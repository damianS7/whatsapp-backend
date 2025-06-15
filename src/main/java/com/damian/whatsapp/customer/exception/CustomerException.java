package com.damian.whatsapp.customer.exception;

import com.damian.whatsapp.common.exception.ApplicationException;

public class CustomerException extends ApplicationException {
    public CustomerException(String message) {
        super(message);
    }
}
