package com.damian.whatsapp.modules.auth;

import com.damian.whatsapp.modules.user.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.dto.request.UserUpdateRequest;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthorizationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    private User user;
    private User admin;

    @BeforeAll
    void setUp() {
        user = User.create()
                   .setEmail("customer@demo.com")
                   .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                   .setRole(UserRole.USER)
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("images/avatar.jpg");
        user.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(user);

        admin = User.create()
                    .setEmail("admin@test.com")
                    .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                    .setRole(UserRole.ADMIN);
        admin.setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(admin);
    }

    @Test
    @DisplayName("Should have access when token is valid")
    void shouldHaveAccessWhenTokenIsValid() throws Exception {
        // given
        final String givenToken = jwtUtil.generateToken(
                user.getEmail(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/users")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + givenToken))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when not authenticated")
    void shouldNotHaveAccessWhenNotAuthenticated() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers/profile"))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token has expired")
    void shouldNotHaveAccessWhenTokenHasExpired() throws Exception {
        // given
        final String expiredToken = jwtUtil.generateToken(
                user.getEmail(),
                new Date(System.currentTimeMillis() - 1000 * 60 * 60)
        );

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserUpdateRequest request = new UserUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .put("/api/v1/profiles/" + user.getId())
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(Exceptions.JWT.TOKEN.EXPIRED))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token is invalid")
    void shouldNotHaveAccessWhenTokenIsInvalid() throws Exception {
        // given
        final String invalidToken = "bad-token";

        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");

        UserUpdateRequest request = new UserUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .put("/api/v1/profiles/" + user.getId())
                       .contentType(MediaType.APPLICATION_JSON)
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                       .content(jsonRequest))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(jsonPath("$.message").value(Exceptions.JWT.TOKEN.INVALID))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should not have access when token email not exists")
    void shouldNotHaveAccessWhenTokenEmailNotExists() throws Exception {
        // given
        final String token = jwtUtil.generateToken(
                "fake-email@demo.com",
                new Date(System.currentTimeMillis() + 1000 * 60 * 60)
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders
                       .get("/api/v1/customers/me/profile")
                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
               .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
