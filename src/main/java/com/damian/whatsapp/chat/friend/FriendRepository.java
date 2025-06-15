package com.damian.whatsapp.chat.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Set<Friend> findAllByCustomerId(Long customerId);
}

