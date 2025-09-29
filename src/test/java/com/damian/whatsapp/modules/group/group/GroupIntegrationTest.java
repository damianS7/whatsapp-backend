package com.damian.whatsapp.modules.group.group;

import com.damian.whatsapp.modules.group.group.dto.response.GroupDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupIntegrationTest extends AbstractIntegrationTest {
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
    @DisplayName("Should get groups that customer belongs to")
    void shouldGetGroups() throws Exception {
        // given
        loginWithUser(user);

        Group group1 = new Group("gaming", "gaming group");
        group1.setOwner(user);
        groupRepository.save(group1);

        Group group2 = new Group("music", "music group");
        group2.setOwner(user);
        groupRepository.save(group2);

        groupMemberRepository.save(
                new GroupMember(user, group1)
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
        GroupDto[] groupsDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GroupDto[].class
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
