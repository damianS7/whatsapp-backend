package com.damian.whatsapp.group;

import com.damian.whatsapp.auth.http.AuthenticationRequest;
import com.damian.whatsapp.auth.http.AuthenticationResponse;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerGender;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.CustomerRole;
import com.damian.whatsapp.group.dto.GroupDTO;
import com.damian.whatsapp.group.member.GroupMember;
import com.damian.whatsapp.group.member.GroupMemberRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Customer customer;
    private String token;

    @BeforeEach
    void setUp() {
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
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
    @DisplayName("Should get groups that customer belongs to")
    void shouldGetGroups() throws Exception {
        // given
        loginWithCustomer(customer);

        Group group1 = new Group("gaming", "gaming group");
        group1.setOwner(customer);
        groupRepository.save(group1);

        Group group2 = new Group("music", "music group");
        group2.setOwner(customer);
        groupRepository.save(group2);

        groupMemberRepository.save(
                new GroupMember(customer, group1)
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/groups")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        GroupDTO[] groupsDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupDTO[].class
        );

        // then
        assertThat(groupsDTO).isNotNull();
        assertThat(groupsDTO.length).isEqualTo(1);
        assertThat(groupsDTO[0].name()).isEqualTo(group1.getName());
    }

    // TODO: shouldGetGroup
    // TODO: shouldCreateGroup
    // TODO: shouldDeleteGroup
    // TODO: shouldSubscribeToGroup
    // TODO: shouldUnsubscribeFromGroup
    // TODO: shouldReceiveMessage
    // TODO: shouldJoinGroup
    // TODO: shouldLeaveGroup
}
