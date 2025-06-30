package com.damian.whatsapp.group;

import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.group.member.GroupMember;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_customer_id", referencedColumnName = "id")
    private Customer owner;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMember> members = new HashSet<>();

    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
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
        return "Group {" +
               "id=" + id +
               ", name=" + name +
               ", description=" + description +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               "}";

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<GroupMember> getMembers() {
        return members;
    }

    public void addMember(GroupMember groupMember) {
        boolean alreadyExists = this.members.stream()
                                            .anyMatch(gm -> gm
                                                    .getMember()
                                                    .getId()
                                                    .equals(groupMember.getMember().getId())
                                            );

        if (!alreadyExists) {
            this.members.add(groupMember);
        }
    }

    public void setMembers(Set<GroupMember> members) {
        this.members = members;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }
}
