package com.damian.whatsapp.group.dto;

public record GroupCustomerOwnerDTO(
        Long customerId,
        String customerName,
        String avatarFilename
) {
}
