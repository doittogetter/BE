package com.doittogether.platform.business.invite;

import com.doittogether.platform.presentation.dto.channel.request.ChannelInviteLinkTestRequest;

public interface InviteLinkService {
    String generateInviteLink(Long channelId);
    String generateInviteLinkTest(Long channelId, ChannelInviteLinkTestRequest request);
    Long validateInviteLink(String inviteCode);
}
