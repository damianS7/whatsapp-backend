package com.damian.whatsapp.modules.chat.dto;

import com.damian.whatsapp.modules.chat.ChatType;

import java.time.Instant;

public record ChatMessageResponse(
        ChatType chatType,
        Long toId,
        // userId o groupId
        Long fromUserId,
        String fromUserName,
        String message,
        Instant timestamp
) {
}
