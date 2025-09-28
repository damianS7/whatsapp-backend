package com.damian.whatsapp.shared.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "contact_user_id", referencedColumnName = "id")
    private User contact;

    @Column
    private Instant createdAt;

    public Contact() {
        this.createdAt = Instant.now();
    }

    public Contact(User user, User contact) {
        this();
        this.user = user;
        this.contact = contact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
               ", userId=" + user.getId() +
               ", contactUserId=" + contact.getId() +
               ", createdAt=" + createdAt +
               "}";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }
}
