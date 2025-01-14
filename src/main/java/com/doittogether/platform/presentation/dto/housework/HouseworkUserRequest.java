package com.doittogether.platform.presentation.dto.housework;

import lombok.Builder;

@Builder
public record HouseworkUserRequest(
        Long userChannelId,
        Long houseworkId,
        String nameOfHousework
) {
    public static HouseworkUserRequest of(Long userChannelId, Long houseworkId, String nameOfHousework){
        return new HouseworkUserRequest(userChannelId,houseworkId,nameOfHousework);
    }
}
