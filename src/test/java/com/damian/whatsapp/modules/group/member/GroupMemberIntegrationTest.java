package com.damian.whatsapp.modules.group.member;

import com.damian.whatsapp.modules.group.group.dto.request.GroupMemberUpdateRequest;
import com.damian.whatsapp.modules.group.group.dto.response.GroupMemberDto;
import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import com.damian.whatsapp.shared.AbstractIntegrationTest;
import com.damian.whatsapp.shared.domain.Group;
import com.damian.whatsapp.shared.domain.GroupMember;
import com.damian.whatsapp.shared.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupMemberIntegrationTest extends AbstractIntegrationTest {
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
    //        groupMemberRepository.deleteAll();
    //        groupRepository.deleteAll();
    //        userAccountRepository.deleteAll();
    //        userRepository.deleteAll();
    //    }

    @Test
    @DisplayName("Should get group members")
    void shouldGetGroupMembers() throws Exception {
        // given
        loginWithUser(user);

        Group group = new Group("gaming", "gaming group");
        group.setOwner(user);
        groupRepository.save(group);

        groupMemberRepository.save(
                new GroupMember(user, group)
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
        GroupMemberDto[] groupMemberDtos = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupMemberDto[].class
        );

        // then
        assertThat(groupMemberDtos).isNotNull();
        assertThat(groupMemberDtos.length).isEqualTo(1);
    }

    @Test
    @DisplayName("Should add group member")
    void shouldAddGroupMember() throws Exception {
        // given
        loginWithUser(user);

        Group group = new Group("gaming", "gaming group");
        group.setOwner(user);
        groupRepository.save(group);

        GroupMemberUpdateRequest request = new GroupMemberUpdateRequest(
                user.getId()
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
        GroupMemberDto groupMemberDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupMemberDto.class
        );

        // then
        assertThat(groupMemberDTO).isNotNull();
    }

    @Test
    @DisplayName("Should delete group member")
    void shouldDeleteGroupMember() throws Exception {
        // given
        loginWithUser(user);

        Group group = new Group("gaming", "gaming group");
        group.setOwner(user);
        groupRepository.save(group);

        User groupMemberUser = User.create()
                                   .setEmail("user-demo.com")
                                   .setPassword(passwordEncoder.encode(RAW_PASSWORD));
        userRepository.save(groupMemberUser);

        GroupMember groupMember = new GroupMember(
                groupMemberUser,
                group
        );

        groupMemberRepository.save(groupMember);

        // when
        mockMvc
                .perform(
                        delete(
                                "/api/v1//groups/{groupId}/members/{userId}",
                                group.getId(),
                                groupMember.getMember().getId()
                        )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NO_CONTENT.value()));
    }

}
