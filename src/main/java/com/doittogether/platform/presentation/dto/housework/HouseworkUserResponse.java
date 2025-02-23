package com.doittogether.platform.presentation.dto.housework;

import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.UserChannel;
import com.doittogether.platform.domain.enumeration.AssigneeStatus;

public record HouseworkUserResponse(
    Long userId,
    String housework,
    AssigneeStatus status
) {
    public static HouseworkUserResponse of(Long userId,String housework,AssigneeStatus status){
        return new HouseworkUserResponse(userId,housework,status);
    }
}
