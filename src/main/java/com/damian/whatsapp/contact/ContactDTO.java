package com.damian.whatsapp.contact;

public record ContactDTO(
        Long id,
        Long contactCustomerId,
        String name,
        String avatarFilename
) {
}
