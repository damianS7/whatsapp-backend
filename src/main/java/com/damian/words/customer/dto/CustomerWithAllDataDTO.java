package com.damian.words.customer.dto;

import com.damian.words.customer.CustomerRole;
import com.damian.words.customer.profile.ProfileDTO;

import java.time.Instant;

public record CustomerWithAllDataDTO(
        Long id,
        String email,
        CustomerRole role,
        ProfileDTO profile,
        Instant createdAt,
        Instant updatedAt
) {
}