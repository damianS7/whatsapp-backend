package com.damian.whatsapp.shared.domain;

import com.damian.whatsapp.modules.group.member.GroupMemberRole;
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
    @JoinColumn(name = "member_user_id")
    private User member;

    @Column
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private GroupMemberRole memberRole;

    public GroupMember() {
        this.memberRole = GroupMemberRole.MEMBER;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public GroupMember(User member, Group group) {
        this(member, group, GroupMemberRole.MEMBER);
    }

    public GroupMember(User member, Group group, GroupMemberRole memberRole) {
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
        return "GroupMember {" +
               "id=" + id +
               ", Group=" + group.toString() +
               ", User=" + member.toString() +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               "}";

    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupMemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(GroupMemberRole memberRole) {
        this.memberRole = memberRole;
    }
}
