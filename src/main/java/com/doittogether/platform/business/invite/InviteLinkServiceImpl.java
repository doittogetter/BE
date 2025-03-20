package com.doittogether.platform.business.invite;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.redis.InviteException;
import com.doittogether.platform.business.redis.RedisSingleDataService;
import com.doittogether.platform.presentation.dto.channel.request.ChannelInviteLinkTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class InviteLinkServiceImpl implements InviteLinkService {

    private final RedisSingleDataService redisSingleDataService;

    private static final String INVITE_LINK_PREFIX = "invite:";
    private static final String INVITE_LINK_LOCK = "@lock";

    private final int inviteLinkTtlMinutes;

    public InviteLinkServiceImpl (
            RedisSingleDataService redisSingleDataService,
            @Value("${spring.data.redis.invite-link.ttl-minutes:10}") int inviteLinkTtlMinutes
    ) {
        this.redisSingleDataService = redisSingleDataService;
        this.inviteLinkTtlMinutes = inviteLinkTtlMinutes;
    }

    @Override
    public String generateInviteLink(Long channelId) {
        try {
            // 1. 기존 초대 링크 조회
            String existingInviteLink = findInviteLinkByChannelId(channelId);

            if (existingInviteLink != null) {
                if (existingInviteLink.endsWith(INVITE_LINK_LOCK)) {
                    return existingInviteLink.replace(INVITE_LINK_LOCK, "");
                }
                // 기존 초대 링크의 TTL 갱신
                String redisKey = INVITE_LINK_PREFIX + existingInviteLink;
                redisSingleDataService.storeDataWithExpiration(redisKey, channelId.toString(), Duration.ofMinutes(inviteLinkTtlMinutes));
                return existingInviteLink;
            }

            // 2. 새로운 초대 링크 생성
            String newInviteLink = RandomAuthCode.generate();
            String redisKey = INVITE_LINK_PREFIX + newInviteLink;
            redisSingleDataService.storeDataWithExpiration(redisKey, channelId.toString(), Duration.ofMinutes(inviteLinkTtlMinutes));
            return newInviteLink;
        } catch (Exception e) {
            log.error("초대 링크 생성 실패 (channelId: {}): {}", channelId, e.getMessage(), e);
            throw new InviteException(ExceptionCode.INVITE_LINK_GENERATION_FAILED);
        }
    }

    @Override
    public String generateInviteLinkTest(Long channelId, ChannelInviteLinkTestRequest request) {
        String redisKey = INVITE_LINK_PREFIX + request.name() + INVITE_LINK_LOCK;

        redisSingleDataService.storeDataWithExpiration(redisKey, channelId.toString(), Duration.ofMinutes(request.ttl()));

        return request.name();
    }

    @Override
    public Long validateInviteLink(String inviteLink) {
        try {
            // Redis에서 초대 링크를 찾을 두 개의 키
            String redisKey = INVITE_LINK_PREFIX + inviteLink;
            String redisKeyWithLock = redisKey + INVITE_LINK_LOCK;

            String channelId = redisSingleDataService.fetchData(redisKey);
            if (StringUtils.isBlank(channelId)) {
                channelId = redisSingleDataService.fetchData(redisKeyWithLock);
            }

            if (StringUtils.isBlank(channelId)) {
                log.warn("유효하지 않은 초대 링크: {}", inviteLink);
                throw new InviteException(ExceptionCode.INVITE_LINK_INVALID);
            }

            return Long.parseLong(channelId);
        } catch (NumberFormatException e) {
            log.error("초대 링크 채널 ID 변환 실패 (inviteLink: {}, value: {})", inviteLink, e.getMessage(), e);
            throw new InviteException(ExceptionCode.INVITE_LINK_CHANNEL_ID_PARSE_FAILED);
        }
    }

    /**
     * Redis에서 channelId와 연결된 초대 코드를 검색
     *
     * @param channelId 채널 ID
     * @return 초대 코드 (없으면 null 반환)
     */
    private String findInviteLinkByChannelId(Long channelId) {
        try {
            for (String key : redisSingleDataService.findKeysByPattern(INVITE_LINK_PREFIX + "*")) {
                String value = redisSingleDataService.fetchData(key);

                if (value != null && Long.parseLong(value) == channelId) {
                    return key.replace(INVITE_LINK_PREFIX, ""); // 초대 코드 반환
                }
            }
            return null;
        } catch (Exception e) {
            throw new InviteException(ExceptionCode.REDIS_KEY_SEARCH_FAILED);
        }
    }
}
