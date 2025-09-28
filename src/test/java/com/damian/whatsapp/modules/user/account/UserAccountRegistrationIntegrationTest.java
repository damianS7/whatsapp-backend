package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.ApiResponse;
import com.damian.whatsapp.shared.util.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountRegistrationIntegrationTest extends AbstractIntegrationTest {
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
    void shouldNotRegisterAccountWhenMissingFields() throws Exception {
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
    void shouldNotRegisterAccountWhenEmailIsNotWellFormed() throws Exception {
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
    void shouldNotRegisterAccountWhenEmailIsTaken() throws Exception {
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
    void shouldNotRegisterAccountWhenPasswordPolicyNotSatisfied() throws Exception {
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
}
