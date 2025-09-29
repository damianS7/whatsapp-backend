package com.damian.whatsapp.shared.domain;

import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "email")
    private String email;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private UserAccountStatus accountStatus;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private Instant updatedAt;

    public UserAccount() {
        this.updatedAt = Instant.now();
        this.role = UserRole.USER;
        this.accountStatus = UserAccountStatus.PENDING_VERIFICATION;
    }

    public UserAccount(User user) {
        this();
        this.user = user;
    }

    public User getOwner() {
        return this.user;
    }

    public UserAccount setOwner(User user) {
        this.user = user;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public String getPassword() {
        return passwordHash;
    }

    public UserAccount setPassword(String password) {
        this.passwordHash = password;
        return this;
    }

    public boolean isEmailVerified() {
        return this.accountStatus.equals(UserAccountStatus.VERIFIED);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserAccount setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserAccount setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UserAccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public UserAccount setAccountStatus(UserAccountStatus userAccountStatus) {
        this.accountStatus = userAccountStatus;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserAccount setEmail(String email) {
        this.email = email;
        return this;
    }
}
