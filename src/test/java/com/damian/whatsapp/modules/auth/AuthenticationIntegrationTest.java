package com.damian.whatsapp.modules.auth;

import com.damian.whatsapp.modules.auth.dto.AuthenticationRequest;
import com.damian.whatsapp.modules.auth.dto.AuthenticationResponse;
import com.damian.whatsapp.modules.user.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.ApiResponse;
import com.damian.whatsapp.shared.util.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationIntegrationTest extends AbstractIntegrationTest {
    private User user;

    @BeforeAll
    void setUp() {
        user = User.create()
                   .setEmail("customer@test.com")
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

    @Test
    @DisplayName("Should login when valid credentials")
    void shouldLoginWhenValidCredentials() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                this.RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // json to AuthenticationResponse
        AuthenticationResponse response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        // then
        assertThat(jwtUtil.extractEmail(response.token())).isEqualTo(user.getEmail());
        assertTrue(jwtUtil.isTokenValid(response.token()));
    }

    @Test
    @DisplayName("Should not login when invalid credentials")
    void shouldNotLoginWhenInvalidCredentials() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                "badPassword"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .post("/api/v1/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not login when email not exist")
    void shouldNotLoginWhenEmailNotExist() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                "nonemail@demo.com",
                "123456"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertEquals(Exceptions.ACCOUNT.BAD_CREDENTIALS, response.getMessage());
    }

    @Test
    @DisplayName("Should not login when account is disabled")
    void shouldNotLoginWhenAccountIsSuspended() throws Exception {
        // given
        User givenUser = new User();
        givenUser.setEmail("disabled-customer@test.com");
        givenUser.setPassword(passwordEncoder.encode(this.RAW_PASSWORD));
        givenUser.setAccountStatus(UserAccountStatus.SUSPENDED);
        userRepository.save(givenUser);

        AuthenticationRequest request = new AuthenticationRequest(
                givenUser.getEmail(), "123456"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertEquals(Exceptions.ACCOUNT.SUSPENDED, response.getMessage());

        // undo changes
        givenUser.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(givenUser);
    }

    @Test
    @DisplayName("Should not login when invalid email format")
    void shouldNotLoginWhenInvalidEmailFormat() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
                "thisIsNotAnEmail",
                "123456"
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response.getErrors().get("email"))
                .asString()
                .contains("must be a well-formed email address");
        assertEquals(Exceptions.COMMON.VALIDATION_FAILED, response.getMessage());
    }

    @Test
    @DisplayName("Should not login when null fields")
    void shouldNotLoginWhenNullFields() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
                null,
                null
        );

        // when
        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response.getErrors().get("password"))
                .asString()
                .contains("must not be blank");

        assertThat(response.getErrors().get("email"))
                .asString()
                .contains("must not be blank");

        assertEquals(Exceptions.COMMON.VALIDATION_FAILED, response.getMessage());
    }

    @Test
    @DisplayName("Should not login when account is not activated")
    void shouldNotLoginWhenAccountIsNotVerified() throws Exception {
        // given
        user.setAccountStatus(UserAccountStatus.PENDING_VERIFICATION);
        userRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest(
                user.getEmail(),
                RAW_PASSWORD
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // json to ApiResponse
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        assertEquals(Exceptions.ACCOUNT.NOT_VERIFIED, response.getMessage());

        // undo changes to customer
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);
    }

}
