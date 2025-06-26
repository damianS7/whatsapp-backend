package com.damian.whatsapp.chat.http;

import java.time.Instant;

public record ChatMessage(
        String chatId,
        Long groupId,
        Long fromCustomerId,
        Long toCustomerId,
        String fromCustomerName,
        String chatType,
        String message,
        Instant timestamp
) {
}
