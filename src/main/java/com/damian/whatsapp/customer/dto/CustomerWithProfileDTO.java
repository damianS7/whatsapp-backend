package com.damian.whatsapp.customer.dto;

import com.damian.whatsapp.customer.CustomerRole;
import com.damian.whatsapp.customer.profile.ProfileDTO;

import java.time.Instant;

public record CustomerWithProfileDTO(
        Long id,
        String email,
        CustomerRole role,
        ProfileDTO profile,
        Instant createdAt,
        Instant updatedAt
) {
}