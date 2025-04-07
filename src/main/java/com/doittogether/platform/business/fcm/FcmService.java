package com.doittogether.platform.business.fcm;

import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.presentation.dto.fcm.RemoveTokenRequest;
import com.doittogether.platform.presentation.dto.fcm.SaveOrUpdateTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.CheckTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.CheckTokenResponse;
import com.doittogether.platform.presentation.dto.reaction.ReactionRequest;

public interface FcmService {

    void saveOrUpdateToken(User loginUser, SaveOrUpdateTokenRequest saveOrUpdateTokenRequest);
    void removeToken(User loginUser, RemoveTokenRequest removeTokenRequest);
    void sendNotification(User loginUser, ReactionRequest notificationRequest);

    CheckTokenResponse isTokenActive(User loginUser, CheckTokenRequest request);
}
