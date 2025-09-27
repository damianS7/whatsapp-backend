package com.damian.whatsapp.modules.notification.web.rest.dto.mapper;

import com.damian.whatsapp.shared.domain.Notification;
import com.damian.whatsapp.modules.notification.web.rest.dto.response.NotificationDto;
import org.springframework.data.domain.Page;

public class NotificationDtoMapper {
    public static NotificationDto map(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getMetadata(),
                notification.getCreatedAt().toString()
        );
    }

    public static Page<NotificationDto> map(Page<Notification> notifications) {
        return notifications
                .map(
                        NotificationDtoMapper::map
                );
    }

}
