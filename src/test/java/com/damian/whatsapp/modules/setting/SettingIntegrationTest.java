package com.damian.whatsapp.modules.setting;

import com.damian.whatsapp.modules.setting.dto.request.SettingUpdateRequest;
import com.damian.whatsapp.modules.setting.dto.response.SettingDto;
import com.damian.whatsapp.modules.user.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.Setting;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.util.JsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SettingIntegrationTest extends AbstractIntegrationTest {

    private User user;

    @BeforeAll
    void setUp() {
        user = User.create()
                   .setEmail("customerA@test.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.USER)
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("avatar.jpg");
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);
    }


    @Test
    @DisplayName("Should get logged customer settings")
    void shouldGetSettings() throws Exception {
        // given
        loginWithUser(user);

        Setting setting1 = new Setting(user, "lang", "en");
        Setting setting2 = new Setting(user, "2fa", "enabled");

        settingRepository.save(setting1);
        settingRepository.save(setting2);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/settings")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        SettingDto[] settings = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                SettingDto[].class
        );

        // then
        assertThat(settings).isNotNull();
        assertEquals(2, settings.length);
    }

    @Test
    @DisplayName("Should update customer settings")
    void shouldUpdateSettings() throws Exception {
        // given
        loginWithUser(user);

        Setting setting1 = new Setting(user, "lang", "en");
        settingRepository.save(setting1);

        SettingUpdateRequest request = new SettingUpdateRequest(
                "es"
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        put("/api/v1/settings/{id}", setting1.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        SettingDto settings = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                SettingDto.class
        );

        // then
        assertThat(settings).isNotNull()
                            .extracting(SettingDto::value)
                            .isEqualTo(request.value());
    }
}