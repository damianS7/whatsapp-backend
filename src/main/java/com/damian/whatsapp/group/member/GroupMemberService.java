package com.damian.whatsapp.group.member;

import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberService(
            GroupMemberRepository groupMemberRepository
    ) {
        this.groupMemberRepository = groupMemberRepository;
    }

    public Set<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }
}
