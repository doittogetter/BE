package com.doittogether.platform.business.fcm;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.fcm.FcmException;
import com.doittogether.platform.domain.entity.FcmToken;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.infrastructure.persistence.fcm.FcmTokenRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.fcm.NotificationRequest;
import com.doittogether.platform.presentation.dto.fcm.SaveOrUpdateTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.ReactionRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmServiceImpl implements FcmService {

    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;

    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    @Override
    public void saveOrUpdateToken(User loginUser, SaveOrUpdateTokenRequest saveOrUpdateTokenRequest) {
        fcmTokenRepository.findByUserAndPlatformType(loginUser, saveOrUpdateTokenRequest.platformType())
                .ifPresentOrElse(existingToken -> {
                    // 토큰이 이미 존재하면 업데이트
                    existingToken.updateToken(saveOrUpdateTokenRequest.token());

                    log.info("FCM 토큰이 업데이트되었습니다. User: {}, Token: {}", loginUser.getUserId(), saveOrUpdateTokenRequest.token());
                }, () -> {
                    // 새로운 토큰 저장
                    FcmToken newToken = FcmToken.of(
                            loginUser,
                            saveOrUpdateTokenRequest.token(),
                            saveOrUpdateTokenRequest.platformType()
                    );
                    fcmTokenRepository.save(newToken);
                    log.info("새로운 FCM 토큰이 저장되었습니다. User: {}, Token: {}", loginUser.getUserId(), saveOrUpdateTokenRequest.token());
                });
    }

    @Override
    public void sendNotification(User loginUser, ReactionRequest reactionRequest) {
        User targetUser = userRepository.findById(reactionRequest.targetUserId())
                .orElseThrow(() -> new FcmException(ExceptionCode.FCM_TARGET_USER_NOT_FOUND));

//        if (loginUser.equals(targetUser)) {
//            /* TODO: 내가 나에게 메세지 전송은 추 후에 막기. */
//        }

        // FCM 토큰 확인
        if (targetUser.getFcmToken() == null || targetUser.getFcmToken().getToken() == null) {
            throw new FcmException(ExceptionCode.FCM_TARGET_TOKEN_NOT_FOUND);
        }

        String token = targetUser.getFcmToken().getToken();
        Message message = NotificationRequest.makeMessage(reactionRequest.notificationRequest(), token);
        try {
            String response = firebaseMessaging.send(message);
            log.info("{} (이)가 {} 에게 FCM 메세지 전송 성공: {}", loginUser.getNickName(), targetUser.getNickName(), response);
        } catch (Exception e) {
            log.error("FCM 메세지 전송 실패: {}", e.getMessage());
            throw new FcmException(ExceptionCode.FCM_MESSAGE_SEND_FAILED);
        }
    }
}
