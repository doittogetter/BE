package com.doittogether.platform.infrastructure.persistence.fcm;

import com.doittogether.platform.domain.entity.FcmToken;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.enumeration.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserAndPlatformType(User user, PlatformType platformType);

    @Query("SELECT ft.token FROM FcmToken ft WHERE ft.user.userId = :userId")
    Optional<String> findTokenByUserId(@Param("userId") Long userId);
}
