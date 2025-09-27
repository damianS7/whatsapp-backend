package com.damian.whatsapp.modules.setting.web.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SettingUpdateRequest(
        @NotNull(message = "value cannot be null")
        @NotBlank(message = "value cannot be blank")
        String value
) {
}
