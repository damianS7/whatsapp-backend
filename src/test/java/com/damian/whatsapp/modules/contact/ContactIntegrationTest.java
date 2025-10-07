package com.damian.whatsapp.modules.contact;

import com.damian.whatsapp.modules.contact.dto.request.ContactCreateRequest;
import com.damian.whatsapp.modules.contact.dto.response.ContactDto;
import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.Contact;
import com.damian.whatsapp.shared.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactIntegrationTest extends AbstractIntegrationTest {

    private User user;

    @BeforeAll
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

    //    @AfterEach
    //    void tearDown() {
    //        contactRepository.deleteAll();
    //        userRepository.deleteAll();
    //    }

    @Test
    @DisplayName("Should get contacts")
    void shouldGetContacts() throws Exception {
        // given
        loginWithUser(user);

        User userContact = User.create()
                               .setEmail("user-contact@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        userRepository.save(userContact);
        contactRepository.save(
                new Contact(user, userContact)
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/contacts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        ContactDto[] contactDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ContactDto[].class
        );

        // then
        //        assertThat(contactDto).isNotNull();
        //        assertThat(contactDto.length).isGreaterThanOrEqualTo(1);
        //        assertThat(contactDto[0].userId()).isEqualTo(userContact.getId());
    }

    @Test
    @DisplayName("Should add a contact")
    void shouldAddContact() throws Exception {
        // given
        loginWithUser(user);

        User contact = User.create()
                           .setEmail("user-contact@demo.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        userRepository.save(contact);

        ContactCreateRequest contactCreateRequest = new ContactCreateRequest(
                contact.getId()
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/contacts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contactCreateRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        ContactDto contactDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ContactDto.class
        );

        // then
        assertThat(contactDto).isNotNull();
        assertEquals(contactDto.userId(), contact.getId());
    }

    @Test
    @DisplayName("Should not add a contact when already exists")
    void shouldNotAddContactWhenAlreadyExists() throws Exception {
        // given
        loginWithUser(user);

        User userContact = User.create()
                               .setEmail("user-contact@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userRepository.save(userContact);

        Contact contact = new Contact(user, userContact);
        contactRepository.save(contact);

        ContactCreateRequest contactCreateRequest = new ContactCreateRequest(
                userContact.getId()
        );

        // when
        mockMvc
                .perform(
                        post("/api/v1/contacts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contactCreateRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(409))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @DisplayName("Should not add a contact when user not found")
    void shouldNotAddContactWhenUserNotFound() throws Exception {
        // given
        loginWithUser(user);

        User userContact = User.create()
                               .setEmail("user-contact@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userRepository.save(userContact);

        ContactCreateRequest contactCreateRequest = new ContactCreateRequest(
                -1L
        );

        // when
        mockMvc
                .perform(
                        post("/api/v1/contacts")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(contactCreateRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @DisplayName("Should delete a contact")
    void shouldDeleteContact() throws Exception {
        // given
        loginWithUser(user);

        User userContact = User.create()
                               .setEmail("user-contact@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userRepository.save(userContact);

        Contact givenContact = new Contact(user, userContact);
        contactRepository.save(givenContact);

        // when
        MvcResult result = mockMvc
                .perform(
                        delete("/api/v1/contacts/{id}", givenContact.getContact().getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andReturn();

        // then
    }

    @Test
    @DisplayName("Should not delete contact when not found")
    void shouldNotDeleteContactWhenNotFound() throws Exception {
        // given
        loginWithUser(user);

        User userContact = User.create()
                               .setEmail("user-contact2@demo.com")
                               .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userRepository.save(userContact);

        // when
        mockMvc
                .perform(
                        delete("/api/v1/contacts/{id}", 25L)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andReturn();

        // then
    }
}
