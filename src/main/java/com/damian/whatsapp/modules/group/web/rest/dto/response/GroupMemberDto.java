package com.damian.whatsapp.modules.group.web.rest.dto.response;

public record GroupMemberDto(
        Long id,
        Long groupId,
        Long customerId,
        String customerName,
        String customerAvatarFilename
) {
}
