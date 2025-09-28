package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.dto.request.UserAccountPasswordResetSetRequest;
import com.damian.whatsapp.modules.user.accounttoken.UserAccountTokenType;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import com.damian.whatsapp.shared.util.JsonHelper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountVerificationIntegrationTest extends AbstractIntegrationTest {
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
    @DisplayName("Should verify account using token")
    void shouldVerifyAccountUsingToken() throws Exception {
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
                                                      .setType(UserAccountTokenType.ACCOUNT_VERIFICATION)
                                                      .setAccount(unverifiedUser.getAccount());
        userAccountTokenRepository.save(givenToken);

        UserAccountPasswordResetSetRequest request = new UserAccountPasswordResetSetRequest(
                "12345678$Xa"
        );

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/verification/{token}", givenToken.getToken())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(JsonHelper.toJson(request)))
               .andDo(print())
               .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        // then
    }

}
