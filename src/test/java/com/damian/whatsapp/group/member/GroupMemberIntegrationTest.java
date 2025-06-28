package com.damian.whatsapp.group.member;

import com.damian.whatsapp.auth.http.AuthenticationRequest;
import com.damian.whatsapp.auth.http.AuthenticationResponse;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerGender;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.CustomerRole;
import com.damian.whatsapp.group.Group;
import com.damian.whatsapp.group.GroupRepository;
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
public class GroupMemberIntegrationTest {
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
    @DisplayName("Should get group members")
    void shouldGetGroupMembers() throws Exception {
        // given
        loginWithCustomer(customer);

        Group group = new Group("gaming", "gaming group");
        group.setOwner(customer);
        groupRepository.save(group);

        groupMemberRepository.save(
                new GroupMember(customer, group)
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/groups/{id}/members", group.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        GroupMemberDTO[] groupMemberDTOS = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupMemberDTO[].class
        );

        // then
        assertThat(groupMemberDTOS).isNotNull();
        assertThat(groupMemberDTOS.length).isEqualTo(1);
    }

    @Test
    @DisplayName("Should add group member")
    void shouldAddGroupMember() throws Exception {
        // given
        loginWithCustomer(customer);

        Group group = new Group("gaming", "gaming group");
        group.setOwner(customer);
        groupRepository.save(group);

        GroupMemberUpdateRequest request = new GroupMemberUpdateRequest(
                customer.getId()
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/groups/{id}/members", group.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        GroupMemberDTO groupMemberDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupMemberDTO.class
        );

        // then
        assertThat(groupMemberDTO).isNotNull();
    }

    // TODO: should delete group member
}
