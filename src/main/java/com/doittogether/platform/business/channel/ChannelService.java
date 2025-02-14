package com.doittogether.platform.business.channel;

import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.channel.request.*;
import com.doittogether.platform.presentation.dto.channel.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ChannelService {

    ChannelListResponse getMyChannels(User loginUser, Pageable pageable);
    ChannelRegisterResponse createChannel(User loginUser, ChannelRegisterRequest request);
    ChannelUpdateResponse updateChannelName(User loginUser, Long channelId, ChannelUpdateRequest request);
    ChannelUserListResponse getChannelUsers(User loginUser, Long channelId, Pageable pageable);
    ChannelInviteLinkResponse generateInviteLink(Long channelId);
    ChannelJoinResponse joinChannelViaInviteLink(User loginUser, String request);
    ChannelKickUserResponse kickUserFromChannel(User loginUser, Long channelId, ChannelKickUserRequest request);

    void leaveChannels(User loginUser, Long... channelIds);
    void leaveChannels(User loginUser, List<Long> channelIds);
}
