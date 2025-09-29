package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountEmailUpdateRequest;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.util.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountIntegrationTest extends AbstractIntegrationTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setEmail("user@demo.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.ADMIN)
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("avatar.jpg");
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userAccountTokenRepository.deleteAll();
        userAccountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() throws Exception {
        // given
        loginWithUser(user);

        UserAccountEmailUpdateRequest givenRequest = new UserAccountEmailUpdateRequest(
                RAW_PASSWORD,
                "user2@test.com"
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/accounts/email")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonHelper.toJson(givenRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn();

        // then
        UserDto userDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserDto.class
        );

        // then
        assertThat(userDto)
                .isNotNull()
                .extracting(
                        UserDto::id,
                        UserDto::email
                ).containsExactly(
                        user.getId(),
                        givenRequest.newEmail()
                );
    }
}
