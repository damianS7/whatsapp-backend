package com.damian.whatsapp.modules.user.user;

import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.modules.user.user.exception.UserImageNotFoundException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.modules.user.user.service.UserImageService;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.infrastructure.storage.FileStorageService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageProcessingService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageUploaderService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageValidationService;
import com.damian.whatsapp.shared.util.ImageTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserImageServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageUploaderService imageUploaderService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageValidationService imageValidationService;

    @Mock
    private ImageProcessingService imageProcessingService;

    @InjectMocks
    private UserImageService userImageService;
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
    @DisplayName("Should get user image")
    void shouldGetUserImage() throws IOException {
        // given
        File givenFile = ImageTestHelper.multipartToFile(
                ImageTestHelper.createDefaultJpg()
        );

        Resource givenResource = new UrlResource(givenFile.toURI());

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(givenFile);
        when(fileStorageService.createResource(givenFile)).thenReturn(givenResource);
        Resource resource = userImageService.getUserImage(user.getId());

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(givenFile.length(), resource.getFile().length());
    }

    @Test
    @DisplayName("Should not get user image when user not found")
    void shouldNotGetUserImageWhenUserNotFound() throws IOException {
        // given

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userImageService.getUserImage(user.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(Exceptions.USER.IMAGE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not get user image when user image is null")
    void shouldNotGetUserImageWhenUserImageIsNull() throws IOException {
        // given
        user.setImageFilename(null);

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserImageNotFoundException exception = assertThrows(
                UserImageNotFoundException.class,
                () -> userImageService.getUserImage(user.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(Exceptions.USER.IMAGE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should upload user image")
    void shouldUploadUserImage() {
        // given
        setUpContext(user);
        MultipartFile givenMultipart = ImageTestHelper.createDefaultJpg();
        File tempFile = ImageTestHelper.multipartToFile(givenMultipart);

        // when
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(imageValidationService).validateImage(any(), any(Long.class), any(String[].class));
        when(imageProcessingService.optimizeImage(any(), any(Integer.class), any(Integer.class))).thenReturn(
                givenMultipart);
        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyString(),
                anyString()
        )).thenReturn(tempFile);

        File uploadedImage = userImageService.uploadUserImage(
                RAW_PASSWORD, givenMultipart
        );

        // then
        assertNotNull(uploadedImage);
        assertEquals(uploadedImage.length(), tempFile.length());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
