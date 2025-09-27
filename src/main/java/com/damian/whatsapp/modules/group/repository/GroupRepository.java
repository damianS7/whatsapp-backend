package com.damian.whatsapp.modules.group.repository;

import com.damian.whatsapp.shared.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Set<Group> findGroupsByOwner_Id(Long ownerId);

    @Query(
            """
                    SELECT gm.group FROM GroupMember gm
                    WHERE gm.member.id = :userId
                    """
    )
    Set<Group> findBelongingGroupsByUserId(@Param("userId") Long userId);
}

