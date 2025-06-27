package com.damian.whatsapp.group.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Set<GroupMember> findByGroupId(Long groupId);
}

