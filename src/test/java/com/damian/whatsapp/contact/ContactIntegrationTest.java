package com.damian.whatsapp.contact;

import com.damian.whatsapp.auth.http.AuthenticationRequest;
import com.damian.whatsapp.auth.http.AuthenticationResponse;
import com.damian.whatsapp.contact.http.ContactCreateRequest;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerGender;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.CustomerRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Customer customer;
    private String token;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        customerRepository.deleteAll();

        customer = new Customer();
        customer.setRole(CustomerRole.CUSTOMER);
        customer.setEmail("customer@test.com");
        customer.setPassword(bCryptPasswordEncoder.encode("123456"));

        customer.getProfile().setFirstName("John");
        customer.getProfile().setLastName("Wick");
        customer.getProfile().setGender(CustomerGender.MALE);
        customer.getProfile().setBirthdate(LocalDate.of(1989, 1, 1));

        customerRepository.save(customer);
    }

    void loginWithCustomer(Customer customer) throws Exception {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                customer.getEmail(), "123456"
        );

        String jsonRequest = objectMapper.writeValueAsString(authenticationRequest);

        // when
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .content(jsonRequest))
                                  .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthenticationResponse.class
        );

        token = response.token();
    }

    @Test
    @DisplayName("Should get contacts")
    void shouldGetContacts() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);
        contactRepository.save(
                new Contact(customer, customerContact)
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
        ContactDTO[] contactDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ContactDTO[].class
        );

        // then
        assertThat(contactDTO).isNotNull();
        assertThat(contactDTO.length).isGreaterThanOrEqualTo(1);
        assertThat(contactDTO[0].customerId()).isEqualTo(customerContact.getId());
    }

    @Test
    @DisplayName("Should add a contact")
    void shouldAddContact() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer contact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(contact);

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
        ContactDTO contactDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ContactDTO.class
        );

        // then
        assertThat(contactDTO).isNotNull();
        assertEquals(contactDTO.customerId(), contact.getId());
    }

    @Test
    @DisplayName("Should not add a contact when already exists")
    void shouldNotAddContactWhenAlreadyExists() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);

        Contact contact = new Contact(customer, customerContact);
        contactRepository.save(contact);

        ContactCreateRequest contactCreateRequest = new ContactCreateRequest(
                customerContact.getId()
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
    @DisplayName("Should not add a contact when customer not found")
    void shouldNotAddContactWhenCustomerNotFount() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);

        ContactCreateRequest contactCreateRequest = new ContactCreateRequest(
                5L
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
        loginWithCustomer(customer);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);

        Contact givenContact = new Contact(customer, customerContact);
        contactRepository.save(givenContact);

        // when
        MvcResult result = mockMvc
                .perform(
                        delete("/api/v1/contacts/{id}", givenContact.getId())
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
        loginWithCustomer(customer);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);

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

    @Test
    @DisplayName("Should not delete contact when not your contact")
    void shouldNotDeleteContactWhenNotYourContact() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer customerA = new Customer(
                "customerA@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerA);

        Customer customerContact = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(customerContact);

        Contact givenContact = new Contact(customerA, customerContact);
        contactRepository.save(givenContact);

        // when
        mockMvc
                .perform(
                        delete("/api/v1/contacts/{id}", givenContact.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(403))
                .andReturn();

        // then
    }


}
