package com.damian.whatsapp.modules.setting.web.rest.dto.mapper;

import com.damian.whatsapp.shared.domain.Setting;
import com.damian.whatsapp.modules.setting.web.rest.dto.response.SettingDto;

import java.util.Set;
import java.util.stream.Collectors;

public class SettingDtoMapper {
    public static SettingDto toSettingDTO(Setting setting) {
        return new SettingDto(
                setting.getId(),
                setting.getSettingKey(),
                setting.getSettingValue()
        );
    }

    public static Set<SettingDto> toSettingDTOList(Set<Setting> settings) {
        return settings
                .stream()
                .map(
                        SettingDtoMapper::toSettingDTO
                ).collect(Collectors.toSet());
    }
}
