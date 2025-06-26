package com.damian.whatsapp.contact;

public record ContactDTO(
        Long id,
        Long customerId,
        String name,
        String avatarFilename
) {
}
