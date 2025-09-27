package com.damian.whatsapp.modules.group.repository;

import com.damian.whatsapp.shared.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Set<GroupMember> findByGroupId(Long groupId);
}

