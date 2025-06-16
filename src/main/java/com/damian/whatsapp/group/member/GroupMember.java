package com.damian.whatsapp.group.member;

import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.group.Group;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "member_customer_id")
    private Customer member;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private GroupMemberRoleType memberRole;

    public GroupMember() {
        this.memberRole = GroupMemberRoleType.MEMBER;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public GroupMember(Customer member, Group group) {
        this(member, group, GroupMemberRoleType.MEMBER);
    }

    public GroupMember(Customer member, Group group, GroupMemberRoleType memberRole) {
        this();
        this.member = member;
        this.group = group;
        this.memberRole = memberRole;
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
        return "Room {" +
               "id=" + id +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';

    }

    public Customer getMember() {
        return member;
    }

    public void setMember(Customer member) {
        this.member = member;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupMemberRoleType getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(GroupMemberRoleType memberRole) {
        this.memberRole = memberRole;
    }
}
