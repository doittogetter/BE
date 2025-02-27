package com.doittogether.platform.infrastructure.persistence.channel;

import com.doittogether.platform.domain.entity.Channel;
import com.doittogether.platform.domain.enumeration.Role;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.entity.UserChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {
    Page<UserChannel> findByUser(User user, Pageable pageable);

    Optional<UserChannel> findByUserAndChannel(User user, Channel channel);

    Page<UserChannel> findByChannel(Channel channel, Pageable pageable);

    boolean existsByUserAndChannel(User user, Channel channel);

    void deleteByUserAndChannel(User user, Channel channel);

    Optional<UserChannel> findFirstByChannelAndRoleNot(Channel channel, Role role);

    @Query("SELECT uc.channel.channelId FROM UserChannel uc WHERE uc.user = :user")
    List<Long> findChannelIdsByUser(@Param("user") User user);

}
