package com.damian.whatsapp.shared.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column
    private String settingKey;

    @Column
    private String settingValue;

    public Setting() {

    }

    public Setting(User user) {
        this.user = user;
    }

    public Setting(User user, String settingKey, String settingValue) {
        this.user = user;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    public static Setting create(User owner) {
        return new Setting(owner);
    }

    public Long getId() {
        return id;
    }

    public Setting setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Setting {" +
               "id=" + id +
               "customerId=" + getCustomer().getId() +
               "settingKey=" + getSettingKey() +
               "settingValue=" + getSettingValue() +
               "}";
    }

    public String getSettingKey() {
        return settingKey;
    }

    public Setting setSettingKey(String key) {
        this.settingKey = key;
        return this;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public Setting setSettingValue(String settingValue) {
        this.settingValue = settingValue;
        return this;
    }

    public User getCustomer() {
        return user;
    }

    public Setting setCustomer(User user) {
        this.user = user;
        return this;
    }

    public boolean isOwner(User user) {
        return this.user.getId().equals(user.getId());
    }
}
