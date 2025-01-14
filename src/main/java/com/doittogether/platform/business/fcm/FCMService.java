package com.doittogether.platform.business.fcm;

import com.doittogether.platform.presentation.dto.fcm.PushRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(PushRequest pushRequest) {
        try {
            Message message = PushRequest.makeMessage(pushRequest);

            String response = firebaseMessaging.send(message);
            log.info("FCM 메세지 전송 성공: {}", response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("FCM 메세지 전송 실패: {}", e.getMessage());
        }
    }
}
