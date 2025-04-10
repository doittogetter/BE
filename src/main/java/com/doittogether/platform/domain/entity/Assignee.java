package com.doittogether.platform.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignee {
    @Id
    @Column(name = "assignee_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long assigneeId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static Assignee of(Long assigneeId, User user){
        final Assignee assignee=new Assignee();
        assignee.assigneeId= assigneeId;
        assignee.user = user;
        return assignee;
    }

    public static Assignee assignAssignee(User user){
        final Assignee assignee = new Assignee();
        assignee.assigneeId = user.getUserId();
        assignee.user = user;
        return assignee;
    }
    public Long retrieveAssigneeId() {
        return assigneeId;
    }
    public User retrieveUser() {
        return user;
    }
}
