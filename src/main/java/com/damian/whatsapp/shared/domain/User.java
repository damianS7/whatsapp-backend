package com.damian.whatsapp.shared.domain;

import com.damian.whatsapp.modules.user.account.account.UserAccountStatus;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.enums.UserRole;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phone;

    @Column
    private LocalDate birthdate;

    @Column
    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(name = "image_filename")
    private String imageFilename;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAccount userAccount;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public User() {
        this.userAccount = new UserAccount(this);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public User(Long userId, String email, String password) {
        this();
        this.id = userId;
        this.userAccount.setEmail(email);
        this.userAccount.setPassword(password);
    }

    public static User create() {
        return new User();
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return this.userAccount.getEmail();
    }

    public User setEmail(String email) {
        this.userAccount.setEmail(email);
        return this;
    }

    public String getPassword() {
        return this.userAccount.getPassword();
    }

    public User setPassword(String password) {
        this.userAccount.setPassword(password);
        return this;
    }

    public UserRole getRole() {
        return this.userAccount.getRole();
    }

    public User setRole(UserRole role) {
        this.userAccount.setRole(role);
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;

    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }


    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public User setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserGender getGender() {
        return gender;
    }

    public User setGender(UserGender gender) {
        this.gender = gender;
        return this;
    }

    public UserAccountStatus getAccountStatus() {
        return this.userAccount.getAccountStatus();
    }

    public User setAccountStatus(UserAccountStatus status) {
        this.userAccount.setAccountStatus(status);
        return this;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public User setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
        return this;
    }

    public UserAccount getAccount() {
        return userAccount;
    }

    public User setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    @Override
    public String toString() {
        return "User {" +
               " id=" + id +
               ", email=" + userAccount.getEmail() +
               ", password=" + userAccount.getPassword() +
               ", account_status=" + userAccount.getAccountStatus() +
               ", role=" + userAccount.getRole() +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';

    }
}
