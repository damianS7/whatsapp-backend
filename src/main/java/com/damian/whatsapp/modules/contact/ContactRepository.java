package com.damian.whatsapp.modules.contact;

import com.damian.whatsapp.shared.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Set<Contact> findAllByUserId(Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.user.id = :userId AND c.contact.id = :contactUserId")
    boolean contactExists(@Param("userId") Long userId, @Param("contactUserId") Long contactUserId);
}

