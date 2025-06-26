package com.damian.whatsapp.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Set<Contact> findAllByCustomerId(Long customerId);

    @Query("SELECT COUNT(c) > 0 FROM Contact c WHERE c.customer.id = :customerId AND c.contact.id = :contactCustomerId")
    boolean contactExists(@Param("customerId") Long customerId, @Param("contactCustomerId") Long contactCustomerId);
}

