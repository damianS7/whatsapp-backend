package com.damian.whatsapp.contact;

import com.damian.whatsapp.customer.Customer;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "customer_contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "contact_customer_id", referencedColumnName = "id")
    private Customer contact;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public Contact() {
    }

    public Contact(Customer customer, Customer contact) {
        this.customer = customer;
        this.contact = contact;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Contact {" +
               "id=" + id +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getContact() {
        return contact;
    }

    public void setContact(Customer contact) {
        this.contact = contact;
    }
}
