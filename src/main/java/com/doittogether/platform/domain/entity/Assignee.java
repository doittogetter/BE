package com.doittogether.platform.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.doittogether.platform.domain.enumeration.HouseworkStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private HouseworkStatus houseworkStatus;

    public static Assignee of(Long assigneeId, User user, HouseworkStatus houseworkStatus){
        final Assignee assignee=new Assignee();
        assignee.assigneeId= assigneeId;
        assignee.user = user;
        assignee.houseworkStatus = houseworkStatus;
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
