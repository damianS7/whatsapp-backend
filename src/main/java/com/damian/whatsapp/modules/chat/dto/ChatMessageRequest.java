package com.damian.whatsapp.modules.chat.dto;

import com.damian.whatsapp.modules.chat.ChatType;

public record ChatMessageRequest(
        ChatType chatType,
        Long toId,
        // userId o groupId
        String message
) {
}
