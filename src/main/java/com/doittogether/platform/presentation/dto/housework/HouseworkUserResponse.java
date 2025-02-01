package com.doittogether.platform.presentation.dto.housework;

import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.UserChannel;

public record HouseworkUserResponse(
    Long userId,
    String housework
) {
    public static HouseworkUserResponse of(Long userId,String housework){
        return new HouseworkUserResponse(userId,housework);
    }
}
