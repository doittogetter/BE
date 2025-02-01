package com.doittogether.platform.infrastructure.persistence.user;

import com.doittogether.platform.domain.entity.Channel;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.entity.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(final String email);

    @Query("SELECT u.profileImage.url FROM User u WHERE u.nickName = :nickName")
    Optional<String> findProfileImageUrlByNickName(@Param("nickName") String nickName);

    User findBySocialId(String socialId);

    @Query("SELECT uc.user FROM UserChannel uc WHERE uc.channel.channelId = :channelId")
    List<User> findByChannelId(@Param("channelId") Long channelId);
}
