package com.damian.whatsapp.modules.user.user;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountEmailTakenException;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.whatsapp.modules.user.user.dto.request.UserUpdateRequest;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.modules.user.user.exception.UserAuthorizationException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.exception.UserUpdateException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.modules.user.user.service.UserService;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create()
                   .setId(2L)
                   .setEmail("user@test.com")
                   .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                   .setRole(UserRole.USER)
                   .setUserName("John")
                   .setFirstName("John")
                   .setLastName("Wick")
                   .setGender(UserGender.MALE)
                   .setBirthdate(LocalDate.of(1989, 1, 1))
                   .setImageFilename("images/avatar.jpg");
    }

    @Test
    @DisplayName("Should get all user")
    void shouldGetAllUsers() {
        // given
        List<User> userList = List.of(
                new User(1L, "user1@test.com", "password1"),
                new User(2L, "user2@test.com", "password2")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        // when
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        Page<User> result = userService.getUsers(pageable);

        // then
        assertNotNull(result);
        assertEquals(userList.size(), result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should create user")
    void shouldCreateUser() {
        // given
        final String passwordHash = "$5554ml;f;lsd";
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // when
        when(bCryptPasswordEncoder.encode(request.password())).thenReturn(passwordHash);
        when(userRepository.existsByUserAccount_Email(request.email())).thenReturn(false);
        userService.createUser(request);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User storedUser = userArgumentCaptor.getValue();
        verify(userRepository, times(1)).save(storedUser);

        assertThat(storedUser)
                .isNotNull()
                .extracting(
                        User::getId,
                        User::getEmail
                ).containsExactly(
                        storedUser.getId(),
                        storedUser.getEmail()
                );
    }

    @Test
    @DisplayName("Should not create any user when email is taken")
    void shouldNotCreateUsersWhenEmailIsTaken() {
        // given
        UserAccountRegistrationRequest request = new UserAccountRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                UserGender.MALE
        );

        // when
        when(userRepository.existsByUserAccount_Email(request.email())).thenReturn(true);
        UserAccountEmailTakenException exception = assertThrows(
                UserAccountEmailTakenException.class,
                () -> userService.createUser(request)
        );

        // then
        verify(userRepository, times(0)).save(any());
        assertEquals(Exceptions.USER.EMAIL_TAKEN, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // given
        Long id = 7L;
        when(userRepository.existsById(id)).thenReturn(true);

        // when
        userService.deleteUser(id);

        // then
        verify(userRepository, times(1)).deleteById(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should not delete user when not exist")
    void shouldNotDeleteUserWhenNotExist() {
        // given
        Long id = -1L;

        // when
        when(userRepository.existsById(id)).thenReturn(false);

        // then
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(id)
        );
        assertEquals(exception.getMessage(), Exceptions.USER.NOT_FOUND);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        // given
        setUpContext(user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("lastName", "David");
        fields.put("birthdate", "1904-01-02");
        fields.put("gender", "MALE");
        fields.put("phone", "9199191919");
        fields.put("avatarFilename", "image.jpg");
        UserUpdateRequest givenRequest = new UserUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(givenRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(givenRequest.fieldsToUpdate().get("firstName"));
        assertThat(result.getLastName()).isEqualTo(givenRequest.fieldsToUpdate().get("lastName"));
        assertThat(result.getPhone()).isEqualTo(givenRequest.fieldsToUpdate().get("phone"));
        assertThat(result.getBirthdate().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("birthdate"));
        assertThat(result.getGender().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("gender"));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should not update user when password is wrong")
    void shouldNotUpdateUserWhenPasswordIsWrong() {
        // given
        setUpContext(user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UserUpdateRequest givenRequest = new UserUpdateRequest(
                "wrongPassword1",
                fields
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userService.updateUser(givenRequest)
        );

        // Then
        assertEquals(Exceptions.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update user when user not found")
    void shouldNotUpdateUserWhenUserNotFound() {
        // given
        setUpContext(user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UserUpdateRequest givenRequest = new UserUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(givenRequest)
        );

        // Then
        assertEquals(Exceptions.USER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update user when user not found")
    void shouldNotUpdateUserWhenYouAreNotOwner() {
        // given
        setUpContext(user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UserUpdateRequest givenRequest = new UserUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        User givenUser = new User(
                5L,
                "user@test.com",
                "12345"
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(givenUser));
        UserAuthorizationException exception = assertThrows(
                UserAuthorizationException.class,
                () -> userService.updateUser(givenRequest)
        );

        // Then
        assertEquals(Exceptions.USER.NOT_OWNER, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update user when user not found")
    void shouldNotUpdateUserWhenInvalidField() {
        // given
        setUpContext(user);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("fakeField", "1234");
        UserUpdateRequest givenRequest = new UserUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserUpdateException exception = assertThrows(
                UserUpdateException.class,
                () -> userService.updateUser(givenRequest)
        );

        // Then
        assertEquals(Exceptions.USER.UPDATE_FAILED_INVALID_FIELD, exception.getMessage());
    }
}
