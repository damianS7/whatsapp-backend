package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.dto.request.UserAccountPasswordResetRequest;
import com.damian.whatsapp.modules.user.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.whatsapp.modules.user.account.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.ApiResponse;
import com.damian.whatsapp.shared.util.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountPasswordIntegrationTest extends AbstractIntegrationTest {
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
    @DisplayName("Should update password")
    void shouldUpdatePassword() throws Exception {
        // given
        loginWithUser(user);

        UserAccountPasswordUpdateRequest updatePasswordRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                "12345678$Xa"
        );

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(updatePasswordRequest)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should update password")
    void shouldNotUpdatePasswordWhenPasswordMismatch() throws Exception {
        // given
        loginWithUser(user);
        UserAccountPasswordUpdateRequest updatePasswordRequest = new UserAccountPasswordUpdateRequest(
                "1234564",
                "12345678$Xa"
        );

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                              .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(updatePasswordRequest)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not update password when password policy not satisfied")
    void shouldNotUpdatePasswordWhenPasswordPolicyNotSatisfied() throws Exception {
        // given
        loginWithUser(user);
        UserAccountPasswordUpdateRequest updatePasswordRequest = new UserAccountPasswordUpdateRequest(
                RAW_PASSWORD,
                "1234"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(JsonHelper.toJson(updatePasswordRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        ApiResponse::getMessage
                ).isEqualTo(
                        Exceptions.COMMON.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("newPassword"))
                .containsIgnoringCase("password must be at least");


    }

    @Test
    @DisplayName("Should not update password when password is null")
    void shouldNotUpdatePasswordWhenPasswordIsNull() throws Exception {
        // given
        loginWithUser(user);
        UserAccountPasswordUpdateRequest updatePasswordRequest = new UserAccountPasswordUpdateRequest(
                "1234564",
                null
        );

        // when
        // then
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.patch("/api/v1/accounts/password")
                                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(JsonHelper.toJson(updatePasswordRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(
                        ApiResponse::getMessage
                ).isEqualTo(
                        Exceptions.COMMON.VALIDATION_FAILED
                );

        assertThat(response.getErrors().get("newPassword"))
                .containsIgnoringCase("must not be blank");
    }

    @Test
    @DisplayName("Should send reset password token to user email")
    void shouldSendResetPasswordToken() throws Exception {
        // given
        UserAccountPasswordResetRequest request = new UserAccountPasswordResetRequest(
                user.getEmail()
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/password/reset")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

    @Test
    @DisplayName("Should reset password using token")
    void shouldResetPasswordUsingToken() throws Exception {
        // given
        User unverifiedUser = User.create()
                                  .setEmail("non-verified-user@demo.com")
                                  .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                                  .setRole(UserRole.USER)
                                  .setFirstName("John")
                                  .setLastName("Wick")
                                  .setGender(UserGender.MALE)
                                  .setBirthdate(LocalDate.of(1989, 1, 1))
                                  .setImageFilename("avatar.jpg");
        user.setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);
        userRepository.save(unverifiedUser);

        UserAccountToken givenToken = UserAccountToken.create()
                                                      .setAccount(unverifiedUser.getAccount());
        userAccountTokenRepository.save(givenToken);

        UserAccountPasswordResetSetRequest request = new UserAccountPasswordResetSetRequest(
                "12345678$Xa"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/password/reset/{token}", givenToken.getToken())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

}
