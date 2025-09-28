package com.damian.whatsapp.modules.setting;

import com.damian.whatsapp.modules.setting.dto.request.SettingUpdateRequest;
import com.damian.whatsapp.modules.setting.exception.SettingNotFoundException;
import com.damian.whatsapp.modules.setting.exception.SettingNotOwnerException;
import com.damian.whatsapp.modules.setting.service.SettingService;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.Setting;
import com.damian.whatsapp.shared.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SettingServiceTest extends AbstractServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    @Test
    @DisplayName("Should get settings for the current customer")
    void shouldGetSettings() {
        // given
        User currentUser = User
                .create()
                .setId(1L)
                .setEmail("customer@demo.com")
                .setPassword(passwordEncoder.encode(RAW_PASSWORD)
                );
        setUpContext(currentUser);

        Set<Setting> givenSettings = Set.of(
                new Setting(currentUser, "key1", "value"),
                new Setting(currentUser, "key2", "value")
        );

        // when
        when(settingRepository.findByUser_Id(currentUser.getId())).thenReturn(givenSettings);
        Set<Setting> result = settingService.getSettings();

        // then
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(Setting::getSettingKey)
                .containsExactlyInAnyOrder("key1", "key2");
        verify(settingRepository, times(1)).findByUser_Id(currentUser.getId());
    }

    @Test
    @DisplayName("Should update a setting")
    void shouldUpdateSetting() {
        // given
        User currentUser = User.create()
                               .setId(1L)
                               .setEmail("customer@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD)
                               );
        setUpContext(currentUser);

        Setting givenSetting = Setting.create(currentUser)
                                      .setId(3L)
                                      .setSettingKey("key1")
                                      .setSettingValue("value");

        SettingUpdateRequest request = new SettingUpdateRequest("newValue");

        // when
        when(settingRepository.findById(givenSetting.getId())).thenReturn(Optional.of(givenSetting));
        when(settingRepository.save(any(Setting.class))).thenReturn(givenSetting);
        Setting settingResult = settingService.updateSetting(givenSetting.getId(), request);

        // then
        assertThat(settingResult)
                .isNotNull()
                .extracting(Setting::getSettingValue)
                .isEqualTo(request.value());
        verify(settingRepository, times(1)).findById(givenSetting.getId());
        verify(settingRepository, times(1)).save(any(Setting.class));
    }

    @Test
    @DisplayName("Should not update a setting when not found")
    void shouldNotUpdateSettingWhenNotFound() {
        // given
        User currentUser = User.create()
                               .setId(1L)
                               .setEmail("customer@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD)
                               );
        setUpContext(currentUser);

        Setting givenSetting = Setting.create(currentUser)
                                      .setId(3L)
                                      .setSettingKey("key1")
                                      .setSettingValue("value");
        SettingUpdateRequest request = new SettingUpdateRequest("newValue");

        // when
        when(settingRepository.findById(givenSetting.getId())).thenReturn(Optional.empty());

        assertThrows(
                SettingNotFoundException.class,
                () -> settingService.updateSetting(givenSetting.getId(), request)
        );

        // then
        verify(settingRepository, times(1)).findById(givenSetting.getId());
    }

    @Test
    @DisplayName("Should not update a setting when not owner")
    void shouldNotUpdateSettingWhenNotOwner() {
        // given
        User currentUser = User.create()
                               .setId(1L)
                               .setEmail("customer@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD)
                               );
        setUpContext(currentUser);

        User settingOwner = User.create()
                                .setId(5L)
                                .setEmail("customer2@demo.com")
                                .setPassword(passwordEncoder.encode(RAW_PASSWORD)
                                );

        Setting givenSetting = Setting.create(settingOwner)
                                      .setId(3L)
                                      .setSettingKey("key")
                                      .setSettingValue("value");

        SettingUpdateRequest request = new SettingUpdateRequest("newValue");

        // when
        when(settingRepository.findById(givenSetting.getId())).thenReturn(Optional.of(givenSetting));

        assertThrows(
                SettingNotOwnerException.class,
                () -> settingService.updateSetting(givenSetting.getId(), request)
        );

        // then
        verify(settingRepository, times(1)).findById(givenSetting.getId());
    }
}
