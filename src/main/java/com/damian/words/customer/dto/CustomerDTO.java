package com.damian.words.customer.dto;

import com.damian.words.customer.CustomerRole;

import java.time.Instant;

public record CustomerDTO(
        Long id,
        String email,
        CustomerRole role,
        Instant createdAt,
        Instant updatedAt
) {
}