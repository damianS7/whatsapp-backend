package com.damian.whatsapp.group.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record GroupUpdateRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Description must not be blank")
        String description,

        @Size(max = 100, message = "Group members must not exceed 100 members")
        Set<Long> membersId
) {
}
