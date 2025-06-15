package com.damian.whatsapp.chat.room.http;

import jakarta.validation.constraints.NotBlank;

public record RoomCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
