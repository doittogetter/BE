package com.doittogether.platform.infrastructure.persistence.fcm;

import com.doittogether.platform.domain.entity.FcmToken;
import com.doittogether.platform.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    // Optional<FcmToken> findByUserAndPlatformType(User user, String platformType);

    // 특정 사용자와 토큰으로 검색 (토큰이 중복되지 않도록)
    Optional<FcmToken> findByUserAndToken(User user, String token);

    @Query("SELECT ft.token FROM FcmToken ft WHERE ft.user.userId = :userId")
    Optional<String> findTokenByUserId(@Param("userId") Long userId);

    List<FcmToken> findAllByUserAndDeletedAtIsNull(User user);

    // 일정 기간 이전에 삭제된 토큰 일괄 삭제
    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.deletedAt < :threshold")
    void deleteAllByDeletedAtBefore(@Param("threshold") LocalDateTime threshold);
}
