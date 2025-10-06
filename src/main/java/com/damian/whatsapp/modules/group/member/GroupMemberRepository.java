package com.damian.whatsapp.modules.group.member;

import com.damian.whatsapp.shared.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupIdAndMember_Id(Long groupId, Long userId);

    Set<GroupMember> findByGroupId(Long groupId);
}

