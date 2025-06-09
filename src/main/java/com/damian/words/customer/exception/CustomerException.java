package com.damian.words.customer.exception;

import com.damian.words.common.exception.ApplicationException;

public class CustomerException extends ApplicationException {
    public CustomerException(String message) {
        super(message);
    }
}
