package com.doittogether.platform.business.fcm;

import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.fcm.SaveOrUpdateTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.ReactionRequest;

public interface FcmService {

    void saveOrUpdateToken(User loginUser, SaveOrUpdateTokenRequest saveOrUpdateTokenRequest);
    void sendNotification(User loginUser, ReactionRequest notificationRequest);
}
