package com.doittogether.platform.presentation.dto.housework;

import lombok.Builder;

@Builder
public record HouseworkUserRequest(
        Long channelId,
        Long houseworkId
) {
    public static HouseworkUserRequest of(Long channelId, Long houseworkId){
        return new HouseworkUserRequest(channelId,houseworkId);
    }
}
