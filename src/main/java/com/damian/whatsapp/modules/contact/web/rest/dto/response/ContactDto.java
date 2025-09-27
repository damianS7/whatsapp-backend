package com.damian.whatsapp.modules.contact.web.rest.dto.response;

public record ContactDto(
        Long id,
        Long userId,
        String name,
        String avatarFilename
) {
}
