package com.damian.whatsapp.modules.contact.dto.response;

public record ContactDto(
        Long id,
        Long userId,
        String name,
        String avatarFilename
) {
}
