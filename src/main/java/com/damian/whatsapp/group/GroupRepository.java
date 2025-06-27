package com.damian.whatsapp.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Set<Group> findGroupsByOwnerCustomerId(@Param("customerId") Long customerId);
}

