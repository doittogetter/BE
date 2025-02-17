package com.doittogether.platform.presentation.dto.housework;

import lombok.Builder;

@Builder
public record HouseworkUserRequest(
        Long channelId,
        String houseworkName
) {
    public static HouseworkUserRequest of(Long channelId, String houseworkName){
        return new HouseworkUserRequest(channelId,houseworkName);
    }
}
