package com.damian.whatsapp.modules.user.user;

import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.dto.request.UserUpdateRequest;
import com.damian.whatsapp.modules.user.user.dto.response.UserDto;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.infrastructure.storage.FileStorageService;
import com.damian.whatsapp.shared.infrastructure.storage.ImageUploaderService;
import com.damian.whatsapp.shared.infrastructure.storage.exception.FileStorageNotFoundException;
import com.damian.whatsapp.shared.util.ImageTestHelper;
import com.damian.whatsapp.shared.util.JsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest extends AbstractIntegrationTest {

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private ImageUploaderService imageUploaderService;

    private User userA;
    private User userB;

    @BeforeAll
    void setUp() {
        userA = User.create()
                    .setEmail("userA@test.com")
                    .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                    .setRole(UserRole.USER)
                    .setFirstName("John")
                    .setLastName("Wick")
                    .setGender(UserGender.MALE)
                    .setBirthdate(LocalDate.of(1989, 1, 1))
                    .setImageFilename("avatar.jpg")
                    .setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(userA);

        userB = User.create()
                    .setEmail("userB@test.com")
                    .setPassword(passwordEncoder.encode(this.RAW_PASSWORD))
                    .setRole(UserRole.USER)
                    .setFirstName("John")
                    .setLastName("Wick")
                    .setGender(UserGender.MALE)
                    .setBirthdate(LocalDate.of(1989, 1, 1))
                    .setImageFilename("avatar.jpg")
                    .setAccountStatus(UserAccountStatus.VERIFIED);
        userRepository.save(userB);
    }

    @Test
    @DisplayName("Should get logged user")
    void shouldGetUser() throws Exception {
        // given
        loginWithUser(userA);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/users")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
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
                        UserDto::id,
                        UserDto::email
                ).containsExactly(
                        userA.getId(),
                        userA.getEmail()
                );
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() throws Exception {
        // given
        loginWithUser(userA);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");
        fields.put("lastName", "white");
        fields.put("userName", "alice77");
        fields.put("phone", "999 999 999");
        fields.put("birthdate", LocalDate.of(1989, 1, 1));
        fields.put("gender", UserGender.FEMALE);

        UserUpdateRequest givenRequest = new UserUpdateRequest(
                this.RAW_PASSWORD,
                fields
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .content(JsonHelper.toJson(givenRequest)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        UserDto userDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                UserDto.class
        );

        assertThat(userDto)
                .isNotNull()
                .extracting(
                        UserDto::firstName,
                        UserDto::lastName,
                        UserDto::userName,
                        UserDto::phone,
                        UserDto::birthdate,
                        UserDto::gender
                ).containsExactly(
                        givenRequest.fieldsToUpdate().get("firstName"),
                        givenRequest.fieldsToUpdate().get("lastName"),
                        givenRequest.fieldsToUpdate().get("userName"),
                        givenRequest.fieldsToUpdate().get("phone"),
                        givenRequest.fieldsToUpdate().get("birthdate"),
                        givenRequest.fieldsToUpdate().get("gender")
                );
    }

    @Test
    @DisplayName("Should get user image")
    void shouldGetUserImage() throws Exception {
        // given
        loginWithUser(userA);

        MultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        mockMvc
                .perform(
                        get("/api/v1/users/{id}/image", userA.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andReturn();
    }

    @Test
    @DisplayName("Should not get user image when not exist")
    void shouldNotGetUserImageWhenNotExist() throws Exception {
        // given
        loginWithUser(userA);

        when(fileStorageService.getFile(anyString(), anyString())).thenThrow(
                FileStorageNotFoundException.class
        );

        mockMvc
                .perform(
                        get("/api/v1/users/{id}/image", userA.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should upload user image")
    void shouldUploadUserImage() throws Exception {
        // given
        loginWithUser(userA);

        MockMultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyString(),
                anyString()
        )).thenReturn(imageFile);

        // when
        MvcResult result = mockMvc
                .perform(
                        multipart("/api/v1/users/image")
                                .file(imageMultipart)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        Resource resource = new ByteArrayResource(content);

        // then
        assertThat(resource).isNotNull();
        assertEquals(resource.contentLength(), imageMultipart.getBytes().length);
        assertEquals(result.getResponse().getContentType(), imageMultipart.getContentType());
    }

    @Test
    @DisplayName("Should not upload user image when file is empty")
    void shouldNotUploadUserImageWhenFileIsEmpty() throws Exception {
        // given
        loginWithUser(userA);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                userA.getImageFilename(),
                "image/jpeg",
                new byte[0]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/users/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not upload image when size exceeds limit")
    void shouldNotUploadImageWhenSizeExceedsLimit() throws Exception {
        // given
        loginWithUser(userA);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                userA.getImageFilename(),
                "image/jpeg",
                new byte[5 * 1024 * 1024 + 1]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/users/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.PAYLOAD_TOO_LARGE.value()));
    }

    @Test
    @DisplayName("Should not upload image when type is not supported")
    void shouldNotUploadImageWhenTypeIsNotSupported() throws Exception {
        // given
        loginWithUser(userA);

        //        MockMultipartFile file = ImageTestHelper.createDefaultBmp();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                userA.getImageFilename(),
                "text/plain",
                new byte[5]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/users/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }
}
