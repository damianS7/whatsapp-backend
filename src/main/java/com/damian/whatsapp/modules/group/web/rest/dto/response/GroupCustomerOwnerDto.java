package com.damian.whatsapp.modules.group.web.rest.dto.response;

public record GroupCustomerOwnerDto(
        Long customerId,
        String customerName,
        String avatarFilename
) {
}
