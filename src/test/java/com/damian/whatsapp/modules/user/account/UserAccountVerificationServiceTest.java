package com.damian.whatsapp.modules.user.account;

import com.damian.whatsapp.modules.user.account.account.UserAccountRepository;
import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountVerificationNotPendingException;
import com.damian.whatsapp.modules.user.account.account.service.UserAccountVerificationService;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenRepository;
import com.damian.whatsapp.modules.user.account.token.UserAccountTokenType;
import com.damian.whatsapp.modules.user.account.token.exception.UserAccountTokenExpiredException;
import com.damian.whatsapp.modules.user.account.token.exception.UserAccountTokenNotFoundException;
import com.damian.whatsapp.modules.user.account.token.exception.UserAccountTokenUsedException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.domain.UserAccount;
import com.damian.whatsapp.shared.domain.UserAccountToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserAccountVerificationServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountTokenRepository userAccountTokenRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAccountVerificationService userAccountVerificationService;

    @Test
    @DisplayName("Should generate account activation token")
    void shouldGenerateAccountActivationToken() {
        // given
        User user = User
                .create()
                .setEmail("user@test.com")
                .setPassword(passwordEncoder.encode(passwordEncoder.encode(RAW_PASSWORD))
                );

        UserAccountToken givenActivationToken = UserAccountToken.create()
                                                                .setAccount(user.getAccount())
                                                                .setToken("activation-token")
                                                                .setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userRepository.findByUserAccount_Email(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(userAccountTokenRepository.findByAccount_Id(user.getAccount().getId()))
                .thenReturn(Optional.of(givenActivationToken));
        when(userAccountTokenRepository.save(any(UserAccountToken.class)))
                .thenReturn(givenActivationToken);

        UserAccountToken generatedToken = userAccountVerificationService.generateVerificationToken(user.getEmail());

        // then
        assertThat(generatedToken)
                .isNotNull()
                .extracting(UserAccountToken::isUsed)
                .isEqualTo(false);

        verify(userAccountTokenRepository, times(1)).findByAccount_Id(user.getAccount().getId());
        verify(userRepository, times(1)).findByUserAccount_Email(user.getEmail());
        verify(userAccountTokenRepository, times(1)).save(any(UserAccountToken.class));
    }

    @Test
    @DisplayName("Should verify token")
    void shouldValidateToken() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(RAW_PASSWORD)
        );

        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(user.getAccount());
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(userAccountToken.getToken())).thenReturn(Optional.of(
                userAccountToken));
        UserAccountToken result = userAccountVerificationService.validateToken(userAccountToken.getToken());

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(userAccountToken.getToken());
        assertEquals(result.getToken(), userAccountToken.getToken());
    }

    @Test
    @DisplayName("Should activate account")
    void shouldVerifyAccount() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(passwordEncoder.encode(RAW_PASSWORD))
        );

        UserAccountToken activationToken = new UserAccountToken(user.getAccount());
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
        when(userAccountTokenRepository.save(any(UserAccountToken.class))).thenReturn(activationToken);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(user.getAccount());
        //        when(userRepository.save(any(User.class))).thenReturn(user);
        userAccountVerificationService.verifyAccount(activationToken.getToken());

        // then
        //        verify(accountRepository, times(1)).save(user);
        assertThat(activationToken.isUsed()).isEqualTo(true);
        assertThat(user.getAccountStatus()).isEqualTo(UserAccountStatus.VERIFIED);
    }

    @Test
    @DisplayName("Should not activate account when account is Suspended")
    void shouldNotVerifyAccountWhenAccountIsSuspended() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(passwordEncoder.encode(RAW_PASSWORD))
        );
        user.setAccountStatus(UserAccountStatus.SUSPENDED);

        UserAccountToken activationToken = new UserAccountToken(user.getAccount());
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerificationService.verifyAccount(activationToken.getToken())
        );
    }

    @Test
    @DisplayName("Should not activate account when account is active")
    void shouldNotVerifyAccountWhenAccountIsActive() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(passwordEncoder.encode(RAW_PASSWORD))
        );
        user.setAccountStatus(UserAccountStatus.VERIFIED);

        UserAccountToken activationToken = new UserAccountToken(user.getAccount());
        activationToken.setToken("sdfsidjgfiosdjfi");
        activationToken.setType(UserAccountTokenType.ACCOUNT_VERIFICATION);

        // when
        when(userAccountTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
        assertThrows(
                UserAccountVerificationNotPendingException.class,
                () -> userAccountVerificationService.verifyAccount(activationToken.getToken())
        );
    }

    @Test
    @DisplayName("Should not verify token when is wrong")
    void shouldNotVerifyAccountWhenTokenIsWrong() {
        // given
        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(
                UserAccountTokenNotFoundException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not verify token when is expired")
    void shouldNotVerifyAccountWhenTokenIsExpired() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(RAW_PASSWORD)
        );

        UserAccountToken userAccountToken = new UserAccountToken();
        userAccountToken.setAccount(user.getAccount());
        userAccountToken.setToken("token");
        userAccountToken.setCreatedAt(Instant.now());
        userAccountToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        userAccountToken.setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenExpiredException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    @DisplayName("Should not verify token when is already used")
    void shouldNotVerifyAccountWhenTokenIsUsed() {
        // given
        User user = new User(
                10L,
                "user@test.com",
                passwordEncoder.encode(RAW_PASSWORD)
        );

        UserAccountToken userAccountToken = UserAccountToken.create()
                                                            .setUsed(true)
                                                            .setAccount(user.getAccount())
                                                            .setCreatedAt(Instant.now())
                                                            .setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                                                            .setToken("token");

        // when
        when(userAccountTokenRepository.findByToken(anyString())).thenReturn(Optional.of(userAccountToken));
        assertThrows(
                UserAccountTokenUsedException.class,
                () -> userAccountVerificationService.validateToken(anyString())
        );

        // then
        verify(userAccountTokenRepository, times(1)).findByToken(anyString());
    }
}
