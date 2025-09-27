package com.damian.whatsapp.modules.contact.http.web.rest.dto.response;

public record ContactDto(
        Long id,
        Long customerId,
        String name,
        String avatarFilename
) {
}
