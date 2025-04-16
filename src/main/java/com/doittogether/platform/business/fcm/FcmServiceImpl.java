package com.doittogether.platform.business.fcm;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.fcm.FcmException;
import com.doittogether.platform.domain.entity.FcmToken;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.infrastructure.persistence.fcm.FcmTokenRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.fcm.NotificationRequest;
import com.doittogether.platform.presentation.dto.fcm.RemoveTokenRequest;
import com.doittogether.platform.presentation.dto.fcm.SaveOrUpdateTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.CheckTokenRequest;
import com.doittogether.platform.presentation.dto.reaction.CheckTokenResponse;
import com.doittogether.platform.presentation.dto.reaction.ReactionRequest;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        String token = saveOrUpdateTokenRequest.token();
        String platformType = saveOrUpdateTokenRequest.platformType();

        // 1. 동일한 토큰이 존재하면 복구 (삭제된 경우)
        fcmTokenRepository.findByUserAndToken(loginUser, token).ifPresentOrElse(existingToken -> {
            if (existingToken.getDeletedAt() != null) {
                existingToken.reactivate();
                log.info("삭제 예정인 FCM 토큰이 복구되었습니다. User: {}, Token: {}", loginUser.getUserId(), token);
            } else {
                log.info("이미 등록된 FCM 토큰입니다. User: {}, Token: {}", loginUser.getUserId(), token);
            }
        }, () -> {
            // 2. 동일한 토큰이 없다면 새로 저장
            FcmToken newToken = FcmToken.of(loginUser, token, platformType);
            fcmTokenRepository.save(newToken);
            log.info("새로운 FCM 토큰이 저장되었습니다. User: {}, Token: {}", loginUser.getUserId(), token);
        });
    }

    @Override
    public void sendNotification(User loginUser, ReactionRequest reactionRequest) {
        User targetUser = userRepository.findById(reactionRequest.targetUserId())
                .orElseThrow(() -> new FcmException(ExceptionCode.FCM_TARGET_USER_NOT_FOUND));

        // 활성화된 FCM 토큰만 DB에서 직접 조회
        List<String> targetTokens = fcmTokenRepository.findAllByUserAndDeletedAtIsNull(targetUser).stream()
                .map(FcmToken::getToken)
                .toList();

        if (targetTokens.isEmpty()) {
            throw new FcmException(ExceptionCode.FCM_TARGET_TOKEN_NOT_FOUND);
        }

        // 한번에 전송
        MulticastMessage message = NotificationRequest.makeMulticastMessage(reactionRequest.notificationRequest(), targetTokens);
        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("{} (이)가 {} 에게 FCM 메세지 전송 (성공: {}, 실패: {})",
                    loginUser.getNickName(),
                    targetUser.getNickName(),
                    response.getSuccessCount(),
                    response.getFailureCount());

            // 실패한 토큰 로그 기록
            if (response.getFailureCount() > 0) {
                List<SendResponse> failedResponses = response.getResponses().stream()
                        .filter(res -> !res.isSuccessful())
                        .toList();

                failedResponses.forEach(res -> log.error("FCM 전송 실패 (토큰: {}): {}",
                        res.getMessageId(),
                        res.getException().getMessage()));
            }
        } catch (Exception e) {
            log.error("FCM 메세지 전송 실패: {}", e.getMessage());
            throw new FcmException(ExceptionCode.FCM_MESSAGE_SEND_FAILED);
        }
    }

    @Transactional
    @Override
    public void removeToken(User loginUser, RemoveTokenRequest removeTokenRequest) {
        fcmTokenRepository.findByUserAndToken(loginUser, removeTokenRequest.token()).ifPresent(FcmToken::markAsDeleted);
    }

    @Transactional
    public void cleanUpOldTokens(LocalDateTime threshold) {
        fcmTokenRepository.deleteAllByDeletedAtBefore(threshold);
    }

    @Override
    public CheckTokenResponse isTokenActive(User loginUser, CheckTokenRequest request) {
        boolean isActive = fcmTokenRepository.findByUserAndToken(loginUser, request.token())
                .filter(fcmToken -> fcmToken.getDeletedAt() == null)
                .isPresent();

        return new CheckTokenResponse(isActive);
    }
}
