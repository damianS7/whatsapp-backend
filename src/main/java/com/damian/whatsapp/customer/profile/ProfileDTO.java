package com.damian.whatsapp.customer.profile;

import com.damian.whatsapp.customer.CustomerGender;

import java.time.Instant;
import java.time.LocalDate;

public record ProfileDTO(
        Long id,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthdate,
        CustomerGender gender,
        String avatarFilename,
        Instant updatedAt
) {
}
