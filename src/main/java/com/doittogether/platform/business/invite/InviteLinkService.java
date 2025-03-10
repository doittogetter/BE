package com.doittogether.platform.business.invite;

public interface InviteLinkService {
    String generateInviteLink(Long channelId, boolean isTest);
    Long validateInviteLink(String inviteCode);
}
