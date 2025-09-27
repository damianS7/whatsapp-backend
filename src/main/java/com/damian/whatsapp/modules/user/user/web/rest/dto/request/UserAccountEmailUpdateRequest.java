package com.damian.whatsapp.modules.user.user.web.rest.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAccountEmailUpdateRequest(
        @NotBlank(message = "Current password must not be blank")
        String currentPassword,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a well-formed email address.")
        String newEmail
) {
}
