package com.damian.words.chat.friend;

import com.damian.words.auth.http.AuthenticationRequest;
import com.damian.words.auth.http.AuthenticationResponse;
import com.damian.words.customer.Customer;
import com.damian.words.customer.CustomerGender;
import com.damian.words.customer.CustomerRepository;
import com.damian.words.customer.CustomerRole;
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
public class FriendIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Customer customer;
    private String token;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should get friends")
    void shouldGetFriends() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer friend = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(friend);

        Friend givenFriend = new Friend(customer, friend);
        friendRepository.save(givenFriend);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/friends")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        FriendDTO[] friendDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FriendDTO[].class
        );

        // then
        assertThat(friendDTO).isNotNull();
        assertThat(friendDTO.length).isGreaterThanOrEqualTo(1);
        assertThat(friendDTO[0].friendCustomerId()).isEqualTo(friend.getId());
    }

    @Test
    @DisplayName("Should add a friend")
    void shouldAddFriend() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer friend = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(friend);


        // when
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/friends/{id}", friend.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        FriendDTO friendDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FriendDTO.class
        );

        // then
        assertThat(friendDTO).isNotNull();
        assertEquals(friendDTO.friendCustomerId(), friend.getId());
    }

    @Test
    @DisplayName("Should delete a friend")
    void shouldDeleteFriend() throws Exception {
        // given
        loginWithCustomer(customer);

        Customer friend = new Customer(
                "contact@test.com",
                bCryptPasswordEncoder.encode("123456")
        );
        customerRepository.save(friend);

        Friend givenFriendship = new Friend(customer, friend);
        friendRepository.save(givenFriendship);

        // when
        MvcResult result = mockMvc
                .perform(
                        delete("/api/v1/friends/{id}", givenFriendship.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(204))
                .andReturn();

        // then
    }

}
