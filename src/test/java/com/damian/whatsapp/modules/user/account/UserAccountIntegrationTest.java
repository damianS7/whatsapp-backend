package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.modules.user.user.web.rest.dto.request.UserAccountEmailUpdateRequest;
import com.damian.whatsapp.modules.user.user.web.rest.dto.request.UserAccountPasswordUpdateRequest;
import com.damian.whatsapp.modules.user.user.web.rest.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.user.web.rest.dto.response.UserDto;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
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

    @Test
    @DisplayName("Should create account when request is valid")
    void shouldRegisterAccountWhenValidRequest() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@gmail.com",
                "12345678X$",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
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
                        UserDto::email,
                        UserDto::firstName,
                        UserDto::lastName,
                        UserDto::userName,
                        UserDto::phone,
                        UserDto::birthdate,
                        UserDto::gender
                ).containsExactly(
                        request.email(),
                        request.firstName(),
                        request.lastName(),
                        request.userName(),
                        request.phone(),
                        request.birthdate(),
                        request.gender()
                );
    }

    @Test
    @DisplayName("Should not register user when missing fields")
    void shouldNotCreateAccountWhenMissingFields() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@test.com",
                "123456",
                "david",
                "david",
                "white",
                "123 123 123",
                null,
                UserGender.MALE
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/register")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getMessage)
                .asString()
                .isEqualTo(Exceptions.COMMON.VALIDATION_FAILED);
    }

    @Test
    @DisplayName("Should not register user when email is not well-formed")
    void shouldNotCreateAccountWhenEmailIsNotWellFormed() throws Exception {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "badEmail",
                "1234567899X$",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/register")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  //                                  .andExpect(jsonPath("$.errors.email").value(containsString(
                                  //                                          "Email must be a well-formed email address")))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
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

        assertThat(response.getErrors().get("email"))
                .contains("must be a well-formed email address");
    }

    @Test
    @DisplayName("Should not register user when email is taken")
    void shouldNotCreateAccountWhenEmailIsTaken() throws Exception {
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                user.getEmail(),
                "12345678X$",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
        ApiResponse<?> response = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<?>>() {
                }
        );

        // then
        assertThat(response)
                .isNotNull()
                .extracting(ApiResponse::getMessage)
                .asString()
                .isEqualTo(Exceptions.USER.EMAIL_TAKEN);
    }

    @Test
    @DisplayName("Should not register user when password policy not satisfied")
    void shouldNotCreateAccountWhenPasswordPolicyNotSatisfied() throws Exception {
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "user@demo.com",
                "123456",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                          .post("/api/v1/accounts/register")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(JsonHelper.toJson(request)))
                                  .andDo(print())
                                  .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                                  .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                  .andReturn();

        // then
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

        assertThat(response.getErrors().get("password"))
                .containsIgnoringCase("password must be at least");
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

    // TODO shouldVerifyAccountWhenValidToken(
    // TODO shouldResetPasswordWhenValidToken(
}
