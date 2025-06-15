package com.damian.whatsapp.customer.dto;

import com.damian.whatsapp.customer.CustomerRole;

import java.time.Instant;

public record CustomerDTO(
        Long id,
        String email,
        CustomerRole role,
        Instant createdAt,
        Instant updatedAt
) {
}