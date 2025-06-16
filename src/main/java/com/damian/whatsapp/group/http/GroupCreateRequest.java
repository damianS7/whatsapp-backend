package com.damian.whatsapp.group.http;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
