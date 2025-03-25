package com.doittogether.platform.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false, length = 50)
    private String platformType;

    public static FcmToken of(User user, String token, String platformType) {
        FcmToken fcmToken = new FcmToken();
        fcmToken.user = user;
        fcmToken.token = token;
        fcmToken.platformType = platformType;
        return fcmToken;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void markAsDeleted() {
        this.recordDeletedAt(LocalDateTime.now());
    }

    public void reactivate() {
        this.recordDeletedAt(null);
    }
}
